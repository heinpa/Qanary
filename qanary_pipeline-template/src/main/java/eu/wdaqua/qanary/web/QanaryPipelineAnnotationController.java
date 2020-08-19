package eu.wdaqua.qanary.web;

import eu.wdaqua.qanary.commons.QanaryMessage;
import eu.wdaqua.qanary.commons.QanaryUtils;
import eu.wdaqua.qanary.exceptions.MissingRequiredConfiguration;
import eu.wdaqua.qanary.exceptions.SparqlQueryFailed;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * controller for Annotations created by components
 */
@Controller
public class QanaryPipelineAnnotationController {

    private static final Logger logger = LoggerFactory.getLogger(QanaryQuestionAnsweringController.class);
    private org.springframework.core.env.Environment environment;

    public QanaryPipelineAnnotationController(@Autowired Environment environment){
        this.environment = environment;
    }


    public URI getEndpointUri() throws URISyntaxException {
        String triplestore = this.environment.getProperty("qanary.triplestore");
        if (triplestore == null) {
            throw new MissingRequiredConfiguration("qanary.triplestore");
        } else {
            return new URI(triplestore);
        }
    }


    @RequestMapping(value = "/numberOfAnnotations/", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<?> getNumberOfAnnotationsForComponent(
            @RequestParam String component,
            @RequestParam String graph
    ) throws URISyntaxException {

        JSONObject json = new JSONObject();
        URI graphUri = URI.create(graph);

        String sparqlGet ="" +
                "PREFIX oa: <http://www.w3.org/ns/openannotation/core/>" +
                "SELECT (COUNT(*) AS ?NumberOfAnnotations)" +
                "(SAMPLE(STR(?componentname)) AS ?ComponentName)" +
                "FROM <"+graph+">" +
                "WHERE {    ?s oa:annotatedBy ?componentname .    " +
                "FILTER REGEX(STR(?componentname), \""+component+"\") . " +
                "}";

        QanaryMessage qanaryMessage= new QanaryMessage( this.getEndpointUri(), graphUri);
        QanaryUtils qanaryUtils = new QanaryUtils(qanaryMessage);

        try {
            logger.info("fetching number of annotations with query: {}",sparqlGet);

            ResultSet annotations = qanaryUtils.selectFromTripleStore(sparqlGet, this.getEndpointUri().toString());
            QuerySolution annotation = annotations.next();

            int annotationCount = 0;
            String componentUrl = null;

            try {
                annotationCount = annotation.get("NumberOfAnnotations").asLiteral().getInt();
                componentUrl = annotation.get("ComponentName").asLiteral().getString();
                logger.info("found {} annotations for component {} on graph {}",annotationCount,component,graph);
            } catch (NullPointerException nullPointer) {
                logger.info("No annotations were found for component {} on graph {}",component,graph);
            }

            json.put("annotationCount", annotationCount);
            json.put("componentUrl", componentUrl);
            json.put("usedGraph", graph);
            json.put("usedQuery", sparqlGet);


        } catch (SparqlQueryFailed queryFailed) {
            logger.error(queryFailed.getMessage());
        }

        return new ResponseEntity<>(json, HttpStatus.OK);
    }
}

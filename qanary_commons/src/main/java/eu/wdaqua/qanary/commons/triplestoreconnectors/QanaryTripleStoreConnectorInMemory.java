package eu.wdaqua.qanary.commons.triplestoreconnectors;

import java.net.URI;

import org.apache.commons.lang.NotImplementedException;
import org.apache.jena.query.Dataset;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.tdb.TDBFactory;
import org.apache.jena.update.UpdateExecutionFactory;
import org.apache.jena.update.UpdateFactory;
import org.apache.jena.update.UpdateRequest;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import eu.wdaqua.qanary.exceptions.SparqlQueryFailed;

/**
 * an in-memory triplestore using Jena TDB, to activate the triplestore set the
 * environment parameter "enable.in-memory-triplestore" to "true"
 * 
 * beta as of qanary.commons v3.3.0: to be used as in-memory triplestore for
 * regular use in Qanary systems
 * 
 * @author AnBo-de
 *
 */
@ConditionalOnProperty(name = "enable.in-memory-triplestore", havingValue = "true")
@Component
public class QanaryTripleStoreConnectorInMemory extends QanaryTripleStoreConnector {

	private Dataset dataset;

	public QanaryTripleStoreConnectorInMemory() {
		this.connect();
		getLogger().warn("QanaryTripleStoreConnectorInMemory initialized. Still beta for using in production.");
	}

	/**
	 * init dataset or reset dataset 
	 */
	@Override
	public void connect() {
		if (dataset != null) {
			dataset.close();
		}
		dataset = TDBFactory.createDataset();
	}

	@Override
	public ResultSet select(String sparql) throws SparqlQueryFailed {
		QueryExecution qexec = QueryExecutionFactory.create(sparql, dataset);
		return qexec.execSelect();
	}

	@Override
	public boolean ask(String sparql) throws SparqlQueryFailed {
		QueryExecution qexec = QueryExecutionFactory.create(sparql, dataset);
		return qexec.execAsk();		
	}

	@Override
	public void update(String sparql, URI graph) throws SparqlQueryFailed {
		throw new NotImplementedException();
	}

	@Override
	public void update(String sparql) throws SparqlQueryFailed {
		UpdateRequest myUpdateRequest = UpdateFactory.create(sparql);
		UpdateExecutionFactory.create(myUpdateRequest, dataset).execute();
	}

	@Override
	public String getFullEndpointDescription() {
		return "This is an in-memory triplestore. For now, it is intended to be used only in unit tests.";
	}

}

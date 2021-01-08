FROM node:9.4

WORKDIR /usr/share/app

EXPOSE 5000 

RUN npm install -g serve

COPY . .

RUN npm install

# remove any windows line endings
RUN sed -i -e 's/\r$//' /usr/share/app/run-app.sh

RUN chmod +x /usr/share/app/run-app.sh

CMD ["sh", "/usr/share/app/run-app.sh"]

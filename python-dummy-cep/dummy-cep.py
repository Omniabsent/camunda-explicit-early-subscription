#!flask/bin/python
from flask import Flask
from flask import request
import uuid
import requests
import _thread
import time

CAMUNDA_MSG_URL = 'http://localhost:8080/engine-rest/message'


app = Flask(__name__)

def generateUUID():
    myuuid = uuid.uuid4()
    return str(myuuid)
	
def postToCamunda(msgname, correlationKey, myuuid):
    print('(THREAD) sleeping 3 seconds...')
    time.sleep(3)
    print('(THREAD) posting to camunda.')
    requests.post(CAMUNDA_MSG_URL, json={"messageName": msgname, "correlationKeys": { correlationKey : {"value" : myuuid, "type": "String"} }})
    print('(THREAD) done.')

@app.route('/cep/getuuid', methods=['GET'])
def get_getuuid():
    myuuid = generateUUID()
    print('correlating via: ' + myuuid)
    return myuuid

@app.route('/cep/subscription', methods=['POST'])
def create_subscription():
    msgname = request.json['msgName']
    correlationKey = request.json['correlationKey']
    print ('Received: ' + msgname + ',' + correlationKey)
    myuuid = generateUUID()
    print('correlating via: ' + myuuid)

    _thread.start_new_thread( postToCamunda, (msgname, correlationKey, myuuid) )

    return myuuid
	 

if __name__ == '__main__':
    app.run(debug=False)

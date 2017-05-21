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
	
def postToCamunda(msgname, processInstanceId):
    print('(THREAD) sleeping 3 seconds...')
    time.sleep(3)
    print('(THREAD) posting to camunda.')
    
    processVariables = {"eventBody" : {"value" : "test1234", "type": "String"}}
    
    myJson = {"messageName": msgname, "processInstanceId": processInstanceId, "processVariables" : processVariables}
    requests.post(CAMUNDA_MSG_URL, json=myJson)
    print('(THREAD) done.')

@app.route('/cep/getuuid', methods=['GET'])
def get_getuuid():
    myuuid = generateUUID()
    print('correlating via: ' + myuuid)
    return myuuid

@app.route('/cep/subscription', methods=['POST'])
def create_subscription():
    msgname = request.json['msgName']
    instanceId = request.json['processInstanceId']
    print ('Received: ' + msgname + ',' + instanceId)
    myuuid = generateUUID()
    print('New subscription with id: ' + myuuid)

    _thread.start_new_thread( postToCamunda, (msgname, instanceId) )

    return myuuid
	 

if __name__ == '__main__':
    app.run(debug=False)

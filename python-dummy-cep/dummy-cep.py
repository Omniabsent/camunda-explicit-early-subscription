#!flask/bin/python
from flask import Flask
from flask import request
from multiprocessing import Process
import uuid
import requests
import _thread, sys
import time
import re

CAMUNDA_MSG_URL = 'http://localhost:8080/engine-rest/message'

runProgram = True
listSubscriptions = [] # list of (myuuid, instanceId, msgname)

# API
app = Flask(__name__)

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
	
	# add to subscriptions
	global listSubscriptions
	print('New subscription ' + str(len(listSubscriptions)) + ' with uuid: ' + myuuid)

	listSubscriptions.append((myuuid, instanceId, msgname))

	return myuuid

def runFlaskServer():
    app.run(debug=False)
	
# GENERAL CODE
def generateUUID():
	myuuid = uuid.uuid4()
	return str(myuuid)

def postMessageToSubscription(subscription, message):
	msgname = subscription[2]
	processInstanceId = subscription[1]
	print ('posting to Camunda')
	processVariables = {"eventBody" : {"value" : message, "type": "String"}}
	
	myJson = {"messageName": msgname, "processInstanceId": processInstanceId, "processVariables" : processVariables}
	requests.post(CAMUNDA_MSG_URL, json=myJson)
	print('POST done.')
	
# User interface
def runUserInterface():
	while runProgram:
		userinput = input("DUMMYCEP>>")
		interpretLine(userinput)

def interpretLine(userinput):
	global listSubscriptions # list of (myuuid, instanceId, msgname)
	if userinput == 'q':
		global runProgram
		runProgram = False
	elif userinput == 'ls':
		i = 0
		for entry in listSubscriptions:
			print(str(i) + ': ' + str(entry))
			i = i+1
	elif re.match('\d+ .+', userinput):
		subscriptionIndex = int(userinput[:userinput.find(' ')])
		if subscriptionIndex in range(0,len(listSubscriptions)):
			message = userinput[(userinput.find(' ')+1):]
			
			# post to camunda
			subscription = listSubscriptions[subscriptionIndex] 
			postMessageToSubscription(subscription, message)
		else:
			print('invalid list index')
	elif userinput == '':
		True #do nothing
	else:
		print('invalid input. type one of the following: q, ls, \'\\d+ .*\'')

# main
if __name__ == '__main__':
	# start the flask API server in a seperate process
	_thread.start_new_thread( runFlaskServer, () )
	time.sleep(2)
	
	# run the user-interface until the user quits
	runUserInterface()
	
	# stop and exit
	print ('Dummy CEP exited.')
	sys.exit(0)
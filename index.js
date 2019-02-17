const functions = require('firebase-functions');

'use strict';

const {
	dialogflow,
	Permission,
} = require('actions-on-google');

const app = dialogflow({debug: true});

exports.dialogflowFirebaseFulfillment = functions.https.onRequest(app);

app.intent('Default Welcome Intent', (conv) => {
	conv.ask(new Permission({
		context: 'To know where incident reports took place',
		permissions: ['NAME', 'DEVICE_PRECISE_LOCATION']
	}));
});

app.intent('actions_intent_PERMISSION', (conv, params, permissionGranted) => {
	if (!permissionGranted) {
		conv.ask('Ok, no worries. When making reports, you will need to specify locations');
	} else {
		conv.user.storage.location = conv.device.location;
		conv.ask('Thank you, what would you like to report?');
	}
});

app.intent('Icy Road', (conv) => {
	if (conv.user.storage.location) {
		conv.close(`Sent a report of icy roads at (${conv.user.storage.location.coordinates.latitude}, ${conv.user.storage.location.coordinates.longitude})`);
	} else {
		conv.close('Did not have permission to access device location');
	}
});


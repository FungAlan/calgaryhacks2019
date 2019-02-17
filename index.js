const functions = require('firebase-functions');

'use strict';

const {
	dialogflow,
	Permission,
} = require('actions-on-google');

const app = dialogflow({debug: true});

exports.dialogflowFirebaseFulfillment = functions.https.onRequest(app);

app.intent('Default Welcome Intent', (conv) => {
	if (!conv.user.storage.returning) {
		conv.ask(new Permission({
			context: 'To know where incident reports took place',
			permissions: ['NAME', 'DEVICE_PRECISE_LOCATION']
		}));
	} else {
		conv.ask(new Permission({
			context: 'Say yes',
			permissions: 'DEVICE_PRECISE_LOCATION'
		}));
	}
});

app.intent('actions_intent_PERMISSION', (conv, params, permissionGranted) => {
	if (!permissionGranted) {
		conv.close('Sorry, we need location data to pinpoint any reports.');
	} else {
		conv.user.storage.returning = true;
		conv.ask('Thank you, what would you like to report?');
	}
});

app.intent('Icy Road', (conv) => {
	const loc = conv.device.location;
	conv.close(`Sent a report of icy roads at (${loc.coordinates.latitude}, ${loc.coordinates.longitude})`);
});

app.intent('Car Accident', (conv) => {
	const loc = conv.device.location;
	conv.close(`Alright, the report of a traffic accident at (${loc.coordinates.latitude}, ${loc.coordinates.longitude}) is on its way.`); 
});

app.intent('Road Construction', (conv) => {
	const loc = conv.device.location;
	conv.close(`Got it, report of construction at (${loc.coordinates.latitude}, ${loc.coordinates.longitude}) has been noted.`); 
});

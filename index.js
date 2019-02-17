const functions = require('firebase-functions');

const admin = require('firebase-admin');
admin.initializeApp();

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
			context: '',
			permissions: 'DEVICE_PRECISE_LOCATION'
		}));
	}
});

app.intent('actions_intent_PERMISSION', (conv, params, permissionGranted) => {
	if (!permissionGranted) {
		conv.close('Sorry, we need location data to pinpoint any reports.');
	} else {
		conv.user.storage.returning = true;
		conv.ask('What would you like to report?');
	}
});

app.intent('Icy Road', (conv) => {
	const loc = conv.device.location;
    conv.close(`Your report has been sent. Thank you!`); //original: (${loc.coordinates.latitude}, ${loc.coordinates.longitude})
    
    // Get a database reference and push the info into the database
    var db = admin.database();
    var ref = db.ref();

    var reportsRef = ref.child("reports");
    reportsRef.push({
        type: "icy_roads",
        lat: loc.coordinates.latitude,
        long: loc.coordinates.longitude,
        time: 0,
        secondary: "" 
        });
});

app.intent('Car Accident', (conv) => {
	const loc = conv.device.location;
    conv.close(`Alright, the report is on its way.`);
    
    // Get a database reference and push the info into the database
    var db = admin.database();
    var ref = db.ref();

    var reportsRef = ref.child("reports");
    reportsRef.push({
        type: "car_accident",
        lat: loc.coordinates.latitude,
        long: loc.coordinates.longitude,
        time: 0,
        secondary: "" 
        });
});

app.intent('Road Construction', (conv) => {
	const loc = conv.device.location;
    conv.close(`Got it, the report's been sent.`);
    
    // Get a database reference and push the info into the database
    var db = admin.database();
    var ref = db.ref();

    var reportsRef = ref.child("reports");
    reportsRef.push({
        type: "road_construction",
        lat: loc.coordinates.latitude,
        long: loc.coordinates.longitude,
        time: 0,
        secondary: "" 
        });
});

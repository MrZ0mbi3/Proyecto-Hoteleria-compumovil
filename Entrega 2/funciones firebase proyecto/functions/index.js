const functions = require("firebase-functions");
const admin = require("firebase-admin");

admin.initializeApp(functions.config().firebase);
// // Create and Deploy Your First Cloud Functions
// // https://firebase.google.com/docs/functions/write-firebase-functions
//

exports.registrarUsuario = functions.https.onRequest((req, response) => {
  const bodyUser = req.body;
  const writeResult = admin.firestore().collection("Usuarios").add(bodyUser);
  response.json({result: "Mensaje con id:"+ writeResult.id + " Agregado"});
});

exports.registrarEmpresa = functions.https.onRequest((request, response) => {
  const empresaBody = request.body;
  const writeResult =admin.firestore().collection("Empresas").add(empresaBody);
  response.json({result: "Message with ID:"+ writeResult.id + "added."});
});

exports.registrarRuta = functions.https.onRequest((request, response) => {
  const rutaBody = request.body;
  const writeResult =admin.firestore().collection("Rutas").add(rutaBody);
  response.json({result: "Message with ID:"+ writeResult.id + "added."});
});


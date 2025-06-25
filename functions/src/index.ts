import * as functions from "firebase-functions/v1";
import * as admin from "firebase-admin";

admin.initializeApp();

exports.sendAppointmentConfirmedNotification = functions
  .region("europe-west1")
  .firestore
  .document("appointments/{appointmentId}")
  .onWrite(async (change, context) => {
    const before = change.before.exists ? change.before.data() : null;
    const after = change.after.exists ? change.after.data() : null;
    const statusChangedToConfirmed =
      (!before && after?.status === "EXPECTED") ||
      (before && after && before.status !== "EXPECTED" && after.status === "EXPECTED");

    if (!statusChangedToConfirmed) return null;

    const userId = after.patientId;
    const doctorId = after.doctorId || "";

    try {
      const userDoc = await admin.firestore().collection("users").doc(userId).get();
      const userFcmToken = userDoc.get("fcmToken");
      const doctorDoc = await admin.firestore().collection("doctors").doc(doctorId).get();
      const doctorFcmToken = doctorDoc.get("fcmToken");

      let doctorName = "your doctor";
      if (doctorDoc.exists) {
        const firstName = doctorDoc.get("name") || "";
        const lastName = doctorDoc.get("surname") || "";
        doctorName = `${firstName} ${lastName}`.trim();
      }
      if (userFcmToken) {
        const userMessage = {
          notification: {
            title: "Appointment Confirmed",
            body: `Your appointment with Dr. ${doctorName} was confirmed!`,
          },
          token: userFcmToken,
        };
        await admin.messaging().send(userMessage);
        console.log("Notification sent to patient:", userFcmToken);
      }
      if (doctorFcmToken) {
        const doctorMessage = {
          notification: {
            title: "New Appointment Confirmed",
            body: `A new appointment was booked by a patient.`,
          },
          token: doctorFcmToken,
        };
        await admin.messaging().send(doctorMessage);
        console.log("Notification sent to doctor:", doctorFcmToken);
      }
    } catch (error) {
      console.error("Error sending notification:", error);
    }

    return null;
  });


export const sendChatNotification = functions.firestore
  .document("chats/{chatId}/messages/{messageId}")
  .onCreate(async (snapshot, context) => {
    const data = snapshot.data();
    if (!data) return;

    const senderId = data.senderId;
    const receiverId = data.receiverId;
    const text = data.text || "Newv message";

    let receiverDoc = await admin.firestore().collection("users").doc(receiverId).get();
    if (!receiverDoc.exists) {
      receiverDoc = await admin.firestore().collection("doctors").doc(receiverId).get();
    }

    const receiverToken = receiverDoc.data()?.fcmToken;

    if (receiverToken) {
      await admin.messaging().send({
        notification: {
          title: "New message",
          body: text,
        },
        token: receiverToken,
      });
    }
  });

package org.example;

import com.fazecast.jSerialComm.SerialPort;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SimpleSerialReader {
    public static void main(String[] args) {
        String portName = "COM5";  // Port de lecture
        SerialPort serialPort = SerialPort.getCommPort(portName);

        try {
            serialPort.openPort();  // Ouvre le port série
            serialPort.setBaudRate(115200);  // Citesse de transmission
            serialPort.setNumDataBits(8);    // Nombre de bits de données
            serialPort.setNumStopBits(1);    // Nombre de bits de stop
            serialPort.setParity(SerialPort.NO_PARITY);  // Pas de parité

            byte[] buffer = new byte[1024];  // Taille du tampon pour lire les données

            while (true) {
                // Vérifier si des données sont disponibles dans le port série
                if (serialPort.bytesAvailable() > 0) {
                    // Lire les données depuis le port série
                    int numRead = serialPort.readBytes(buffer, buffer.length);
                    if (numRead > 0) {
                        // Obtenir l'heure actuelle et la formater
                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String formattedDate = now.format(formatter);

                        // Convertir le buffer d'UID en une chaîne de valeurs hexadécimales
                        StringBuilder uidString = new StringBuilder();
                        for (int i = 0; i < numRead; i++) {
                            uidString.append(String.format("%02X", buffer[i]));  // Conversion en hexadécimal
                            //if (i < numRead - 1) uidString.append(",");  // Ajouter une virgule entre les valeurs
                        }

                        // Créer l'objet JSON pour l'UID et l'heure
                        JSONObject uidJson = new JSONObject();
                        uidJson.put("UID", uidString.toString());  // Mettre l'UID sous forme de chaîne hexadécimale
                        uidJson.put("time", formattedDate);  // Ajouter l'heure

                        // Créer le JSON principal
                        JSONObject jsonObj = new JSONObject();
                        jsonObj.put("UID", uidJson);  // Mettre l'UID avec son heure dans l'objet principal

                        // Afficher l'objet JSON sous forme de chaîne
                        System.out.println(jsonObj.toString());
                    }
                } else {
                    // Aucune donnée disponible, donc on peut attendre un peu avant de vérifier à nouveau
                    Thread.sleep(100); // Attente de 100 ms pour éviter d'utiliser trop de ressources CPU
                }
            }
        } catch (Exception ex) {
            System.out.println("Erreur de communication série : " + ex.getMessage());
        } finally {
            if (serialPort.isOpen()) {
                serialPort.closePort();  // Ferme le port série à la fin
            }
        }
    }
}

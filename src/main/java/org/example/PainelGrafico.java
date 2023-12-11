package org.example;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import static spark.Spark.post;

public class PainelGrafico {

    JFrame frame = new JFrame("SERVIDOR");
    JPanel panel = new JPanel();
    JTextField inputFieldMassa = new JTextField(10);
    JTextField inputFieldAltura = new JTextField(10);
    JTextField inputFieldIMC = new JTextField(10);
    JLabel labelMassa = new JLabel("Massa:");
    JLabel labelAltura = new JLabel("Altura:");
    JLabel labelIMC = new JLabel("IMC:");

    public PainelGrafico() {
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 500);
        frame.setLocationRelativeTo(null);

        panel.setLayout(new GridLayout(4, 2));

        inputFieldMassa.setFont(new Font("Arial", Font.PLAIN, 30));
        inputFieldAltura.setFont(new Font("Arial", Font.PLAIN, 30));
        inputFieldIMC.setFont(new Font("Arial", Font.PLAIN, 30));

        labelMassa.setFont(new Font("Arial", Font.PLAIN, 30));
        labelAltura.setFont(new Font("Arial", Font.PLAIN, 30));
        labelIMC.setFont(new Font("Arial", Font.PLAIN, 30));

        panel.add(labelMassa);
        panel.add(inputFieldMassa);

        panel.add(labelAltura);
        panel.add(inputFieldAltura);

        panel.add(labelIMC);
        panel.add(inputFieldIMC);


        String[] buttonLabels = {
                "Enviar", "Limpar"
        };

        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.setFont(new Font("Arial", Font.PLAIN, 30));
            panel.add(button);

            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (label.equals("Limpar")) {
                        inputFieldMassa.setText("");
                        inputFieldAltura.setText("");
                        inputFieldIMC.setText("");
                    } else if (label.equals("Enviar")) {
                        String path = "http://localhost:8080/api";

                        try {
                            String massaInserida = inputFieldMassa.getText();
                            String alturaInserida = inputFieldAltura.getText();

                            String content="{ \"massa\": \" " + massaInserida + "\", \"altura\": \"" + alturaInserida + " \" }";

                            System.out.println(content);

                            URL url = new URL(path);
                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                            connection.setRequestMethod("POST");
                            connection.setRequestProperty("Content-Type","application/json");
                            connection.setDoOutput(true);

                            DataOutputStream out = new DataOutputStream( connection.getOutputStream() );
                            out.writeBytes(content);
                            out.flush();
                            out.close();
                            int responseCode = connection.getResponseCode();
                            System.out.println("Code: " + responseCode);
                            if (responseCode != HttpURLConnection.HTTP_OK)
                            {
                                System.out.println("Got an unexpected response code");
                            }


                            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                            String line;
                            while ((line = in.readLine()) != null)
                            {
                                System.out.println(line);
                                JsonElement jsonElement = JsonParser.parseString(line);
                                JsonObject jsonObject = jsonElement.getAsJsonObject();
                                inputFieldIMC.setText(jsonObject.get("IMC").getAsString());
                            }

                            in.close();
                        } catch (MalformedURLException ex) {
                            ex.printStackTrace();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        } catch(NumberFormatException ex){
                            inputFieldIMC.setText("Erro: Entrada invalida");
                        }

                    }
                }
            });
        }

        frame.add(panel);
        frame.setVisible(true);
    }
}

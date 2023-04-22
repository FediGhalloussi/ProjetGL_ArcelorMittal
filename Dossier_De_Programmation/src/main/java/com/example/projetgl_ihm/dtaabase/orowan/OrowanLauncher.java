package com.example.projetgl_ihm.dtaabase.orowan;
import java.io.*;

/**
 * Cette classe permet d'excuter le .exe Orowan
 */
public class OrowanLauncher {

    public OrowanLauncher() {

    }
    /**
     * Lance l'exécution de l'application Orowan en ligne de commande avec les paramètres spécifiés.
     *
     * @param pathInput  Le chemin vers le fichier d'entrée de l'application.
     * @param pathOutput Le chemin vers le fichier de sortie de l'application.
     * @throws IOException            Si une erreur d'entrée/sortie se produit lors de l'exécution de l'application.
     * @throws InterruptedException   Si le thread en cours est interrompu pendant l'exécution de l'application.
     */
    public void launch(String pathInput, String pathOutput) throws IOException, InterruptedException {
        String[] arguments = {"com/example/projetgl_ihm/dtaabase/orowan/Orowan_x64.exe.exe", "i", "c",pathInput, pathOutput};

        try {
            // Créer un processus pour l'exécutable
            ProcessBuilder builder = new ProcessBuilder(arguments);

            // Rediriger la sortie standard et d'erreur vers la console
            builder.redirectOutput(ProcessBuilder.Redirect.INHERIT);
            builder.redirectError(ProcessBuilder.Redirect.INHERIT);

            // Démarrer le processus
            Process process = builder.start();
            // Obtenir le flux de sortie de l'exécutable
            OutputStream stdin = process.getOutputStream();

            // Écrire la commande "i" sur le flux stdin
            String input = "i";
            stdin.write(input.getBytes());
            stdin.write(System.lineSeparator().getBytes());
            stdin.flush();

// Écrire la commande "c" sur le flux stdin
            input = "c";
            stdin.write(input.getBytes());
            stdin.write(System.lineSeparator().getBytes());
            stdin.flush();

            input = pathInput;
            stdin.write(input.getBytes());
            stdin.write(System.lineSeparator().getBytes());
            stdin.flush();

            input = pathOutput;
            stdin.write(input.getBytes());
            stdin.write(System.lineSeparator().getBytes());
            stdin.flush();

// Attendre 10 secondes
            Thread.sleep(5000);

            // Forcer la fermeture de l'exécutable
            process.destroy();

            // Attendre que le processus se termine
            int exitCode = process.waitFor();
            if (exitCode == 0) {
                System.out.println("L'exécutable s'est terminé avec succès.");
            } else {
                System.err.println("L'exécutable s'est terminé avec une erreur. Code de sortie : " + exitCode);
            }

        } catch (IOException e) {
            System.err.println("Erreur lors de l'exécution de l'exécutable : " + e.getMessage());
        } catch (InterruptedException e) {
            System.err.println("Le processus a été interrompu : " + e.getMessage());
        }
    }
}


import java.io.*;
import java.net.*;
import java.util.*;

public class FileServer {
    private static final int PORT = 5000;
    private static final String FILE_DIR = "server_files";  // Directory to store files
    private static final String FILE_LIST_PATH = "file_list.txt"; // Path to store file list

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is running on port " + PORT + "...");
            
            // Create the directory if it doesn't exist
            File dir = new File(FILE_DIR);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // Load the file list on startup
            loadFileList();

            while (true) {
                try {
                    // Accept client connection
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("Client connected: " + clientSocket.getInetAddress());

                    // Handle client in a new thread
                    new Thread(() -> handleClient(clientSocket)).start();
                } catch (IOException e) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("Error starting the server: " + e.getMessage());
        }
    }

    private static void handleClient(Socket clientSocket) {
        try (InputStream input = clientSocket.getInputStream();
             OutputStream output = clientSocket.getOutputStream();
             DataInputStream dataInput = new DataInputStream(input);
             DataOutputStream dataOutput = new DataOutputStream(output)) {

            String command = dataInput.readUTF();
            System.out.println("Received command: " + command);

            if (command.startsWith("UPLOAD")) {
                // Handle file upload
                String fileName = dataInput.readUTF();
                long fileSize = dataInput.readLong();

                System.out.println("Starting upload for file: " + fileName + " with size: " + fileSize);

                // Prepare the file to store
                File file = new File(FILE_DIR, fileName);
                try (FileOutputStream fileOutput = new FileOutputStream(file)) {
                    byte[] buffer = new byte[4096];
                    long bytesRead = 0;
                    while (bytesRead < fileSize) {
                        int read = input.read(buffer);
                        if (read == -1) {
                            throw new IOException("Unexpected end of stream during file upload.");
                        }
                        fileOutput.write(buffer, 0, read);
                        bytesRead += read;
                    }

                    // After uploading, add the file to the list
                    addFileToList(fileName);

                    dataOutput.writeUTF("UPLOAD_SUCCESS");
                    System.out.println("File uploaded successfully: " + fileName);
                } catch (IOException e) {
                    dataOutput.writeUTF("UPLOAD_FAILED");
                    System.err.println("File upload failed for " + fileName + ": " + e.getMessage());
                }

            } else if (command.startsWith("LIST_FILES")) {
                // Handle file list request
                listFiles(dataOutput);

            } else if (command.startsWith("DOWNLOAD")) {
                // Handle file download
                String fileName = dataInput.readUTF();
                System.out.println("Client requested download for file: " + fileName);

                File file = new File(FILE_DIR, fileName);

                if (file.exists() && file.isFile()) {
                    dataOutput.writeUTF("DOWNLOAD_READY");
                    dataOutput.writeLong(file.length());

                    try (FileInputStream fileInput = new FileInputStream(file)) {
                        byte[] buffer = new byte[4096];
                        int read;
                        while ((read = fileInput.read(buffer)) != -1) {
                            output.write(buffer, 0, read);
                        }
                        System.out.println("File sent successfully: " + fileName);
                    } catch (IOException e) {
                        dataOutput.writeUTF("FILE_TRANSFER_FAILED");
                        System.err.println("Error during file transfer for " + fileName + ": " + e.getMessage());
                    }
                } else {
                    dataOutput.writeUTF("FILE_NOT_FOUND");
                    System.err.println("File not found: " + fileName);
                }
            } else {
                dataOutput.writeUTF("INVALID_COMMAND");
                System.err.println("Received invalid command from client.");
            }
        } catch (SocketException e) {
            System.err.println("Client disconnected unexpectedly: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
        }
    }

    // Add file name to the list in the file
    private static void addFileToList(String fileName) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_LIST_PATH, true))) {
            writer.write(fileName);
            writer.newLine();  // Add a newline for each file
        } catch (IOException e) {
            System.err.println("Error updating file list: " + e.getMessage());
        }
    }

    // Send the file list to the client
    private static void listFiles(DataOutputStream dataOutput) throws IOException {
        File fileList = new File(FILE_LIST_PATH);
        if (fileList.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileList))) {
                List<String> files = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    files.add(line);
                }

                // Send the number of files
                dataOutput.writeInt(files.size());

                // Send file names
                for (String fileName : files) {
                    dataOutput.writeUTF(fileName);
                }
            }
        }
    }

    // Load the file list when the server starts
    private static void loadFileList() {
        File fileList = new File(FILE_LIST_PATH);
        if (fileList.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileList))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("Loaded file: " + line);
                }
            } catch (IOException e) {
                System.err.println("Error loading file list: " + e.getMessage());
            }
        }
    }
}

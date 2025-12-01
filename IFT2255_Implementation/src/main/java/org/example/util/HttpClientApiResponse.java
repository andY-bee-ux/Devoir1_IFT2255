package org.example.util;

public class HttpClientApiResponse {
    // Ce code provient du repertoire GitHub : ift2255-template-javalin fournit pour ce projet.
    private final int statusCode;       //Code retourné lors de l'appel d'une requête.
    private final String statusMessage;     //Message retourné lors de l'appel d'une requête.
    private final String body;      //Corps du message envoyé lors de l'appel d'une requête.

    /**
     *  Constructeur de la classe HttpClientApiResponse.
     * @param statusCode Code retourné lors de l'appel d'une requête.
     * @param message Message retourné lors de l'appel d'une requête.
     * @param body Corps du message envoyé lors de l'appel d'une requête.
     **/
    public HttpClientApiResponse(int statusCode, String message, String body) {
        this.statusCode = statusCode;
        this.statusMessage = message;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "Status Code: " + statusCode + ", Message: " + statusMessage + ", Body: " + body;
    }
}

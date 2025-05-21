public class Response {
    private int message_size;
    private String header;
    private String body;

    public Response(int message_size, String header, String body) {
        this.message_size = message_size;
        this.header = header;
        this.body = body;
    }

}
    
    
    
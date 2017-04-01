package me.uptop.mvpgoodpractice.data.network.error;

public class ApiError extends Throwable {
    private int statusCode;
    private String message;

//    @Override
//    public String getMessage() { return message;}

//    private int statusCode;

    public ApiError(int statusCode) {
        super("status code : " + statusCode);
        this.statusCode = statusCode;
    }

    public ApiError(String message) {
        super(message);
    }

    public ApiError() {
    }
}

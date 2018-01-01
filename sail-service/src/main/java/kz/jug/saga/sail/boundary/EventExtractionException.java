package kz.jug.saga.sail.boundary;

class EventExtractionException extends RuntimeException {
    EventExtractionException(String message, Throwable cause) {
        super(message, cause);
    }
}

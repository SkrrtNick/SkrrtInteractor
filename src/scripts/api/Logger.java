package scripts.api;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.tribot.api.General;
@Setter @Getter @Accessors(chain = true)
public class Logger {

    private String header;
    private String message;
    private Loggable loggable = Loggable.MESSAGE;
    private boolean system;

    public void print() {
        if (isSystem()) {
            System.out.println("[" + this.header + "] " + this.message);
        } else {
            General.println("[" + this.header + "] " + this.message, this.getLoggable().getForegroundColor(), this.getLoggable().getBackgroundColor());
        }
        if(loggable.equals(Loggable.FATAL_ERROR)){
            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
            for(StackTraceElement stackTraceElement:stackTraceElements){
                General.println("[StackTrace] " + stackTraceElement.toString(), this.getLoggable().getForegroundColor(), this.getLoggable().getBackgroundColor());
            }
        }
    }

}

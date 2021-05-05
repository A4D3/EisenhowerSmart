package be.demeurea.eisenhowersmart.utils;

import android.app.Dialog;
import android.content.Context;
import android.widget.Button;
import android.widget.TextView;

import be.demeurea.eisenhowersmart.R;

/**
 * Class to make a pop up
 * @author Demeure Arnaud
 * @created on 15-02-21
 */
public class CustomPopup extends Dialog {

    //Properties
    String title;
    String message;
    String warning;
    Button yesBtn, noBtn;
    TextView titleView, messageView, warningView;

    //Constructor

    /**
     * Constructor
     * @param context Context
     */
    public CustomPopup(Context context) {
        super(context, R.style.Widget_AppCompat_PopupMenu);

        setContentView(R.layout.popup);

        this.title = "Titre";
        this.message = "Message";
        this.warning = "Warning";
        this.yesBtn = (Button) findViewById(R.id.yes);
        this.noBtn = (Button) findViewById(R.id.no);
        this.titleView = (TextView) findViewById(R.id.title_popup);
        this.messageView = (TextView) findViewById(R.id.message);
        this.warningView = (TextView) findViewById(R.id.warning);
    }

    //Setter

    /**
     * Set the title.
     * @param title String
     */
    public void setTitle(String title) {
        assert title != null : "CustomPopup.setTitle: title is null.";
        this.title = title;
    }

    /**
     * Set the message.
     * @param message String
     */
    public void setMessage(String message) {
        assert message != null : "CustomPopup.setMessage: message is null.";
        this.message = message;
    }

    /**
     * Set the warning.
     * @param warning String
     */
    public void setWarning(String warning) {
        assert warning != null : "CustomPopup.setWarning: warning is null.";
        this.warning = warning;
    }

    /**
     * Get the positive button.
     * @return yesBtn Button
     */
    public Button getYesBtn() {
        return yesBtn;
    }

    /**
     * Get the negative button.
     * @return noBtn Button
     */
    public Button getNoBtn() {
        return noBtn;
    }

    /**
     * Build the dialog.
     */
    public void build(){
        show();
        titleView.setText(title);
        messageView.setText(message);
        warningView.setText(warning);
    }
}

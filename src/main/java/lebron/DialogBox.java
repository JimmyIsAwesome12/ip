package lebron;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * One row of the conversation: a speech-bubble label next to a round avatar.
 * Created only through {@link #getUserDialog(String, Image)} and
 * {@link #getLebronDialog(String, Image)}, which lay the two speakers out as
 * mirror images of each other.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image image) {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/DialogBox.fxml"));
        fxmlLoader.setController(this);
        fxmlLoader.setRoot(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new AssertionError("DialogBox.fxml failed to load", e);
        }

        dialog.setText(text);
        displayPicture.setImage(image);
    }

    /** Mirrors the row (avatar on the left) so Lebron's messages read as replies. */
    private void flip() {
        setAlignment(Pos.TOP_LEFT);
        List<Node> children = new ArrayList<>(getChildren());
        Collections.reverse(children);
        getChildren().setAll(children);
    }

    /**
     * Returns a dialog box for something the user typed.
     *
     * @param text the user's input
     * @param image the user's avatar
     * @return the dialog box, avatar on the right
     */
    public static DialogBox getUserDialog(String text, Image image) {
        return new DialogBox(text, image);
    }

    /**
     * Returns a dialog box for one of Lebron's responses.
     *
     * @param text Lebron's response text
     * @param image Lebron's avatar
     * @return the dialog box, avatar on the left
     */
    public static DialogBox getLebronDialog(String text, Image image) {
        DialogBox dialogBox = new DialogBox(text, image);
        dialogBox.flip();
        return dialogBox;
    }
}

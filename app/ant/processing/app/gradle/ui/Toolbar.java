package processing.app.gradle.ui;

import processing.app.ui.Editor;
import processing.app.ui.EditorToolbar;

import javax.swing.*;

public class Toolbar {
    Toolbar(Editor editor){}
    public static JComponent legacyWrapped(Editor editor, EditorToolbar toolbar){
        return toolbar;
    }
}

package de.tum.cit.ase.maze.utils;

import com.badlogic.gdx.files.FileHandle;
import games.spooky.gdx.nativefilechooser.*;
import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.nfd.NFDFilterItem;
import org.lwjgl.util.nfd.NativeFileDialog;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.memAllocPointer;
import static org.lwjgl.system.MemoryUtil.memFree;
import static org.lwjgl.util.nfd.NativeFileDialog.NFD_FreePath;
import static org.lwjgl.util.nfd.NativeFileDialog.NFD_GetError;

/**
 * The FixedDesktopFileChooser class is an implementation of the NativeFileChooser interface that uses
 * the NativeFileDialog library for choosing files on desktop platforms.
 */
public class FixedDesktopFileChooser implements NativeFileChooser {
    /**
     * Choose a file using the NativeFileDialog library.
     *
     * @param configuration The configuration for the file chooser.
     * @param callback The callback to handle the result of the file chooser.
     */
    @Override
    public void chooseFile(NativeFileChooserConfiguration configuration, NativeFileChooserCallback callback) {
        NativeFileChooserUtils.checkNotNull(configuration, "configuration");
        NativeFileChooserUtils.checkNotNull(callback, "callback");


        PointerBuffer path = memAllocPointer(1);


        try (MemoryStack stack = stackPush()) {
            NFDFilterItem.Buffer filterList = null;
            if (configuration.mimeFilter != null && !configuration.mimeFilter.isEmpty()) {
                var filter = configuration.mimeFilter.split("[;]");
                filterList = NFDFilterItem.malloc(filter.length);
                for (int i = 0; i < filter.length; i++) {
                    var s = filter[i].split("/");
                    filterList.get(i)
                            .name(stack.UTF8(s[0]))
                            .spec(stack.UTF8(s[1]));

                }
            }
            // Let the os decide what default path to choose! No? Here configuration.directory.file().getPath()
            int result = configuration.intent == NativeFileChooserIntent.SAVE ?
                    NativeFileDialog.NFD_SaveDialog(path, filterList, null, configuration.title) :
                    NativeFileDialog.NFD_OpenDialog(path, filterList, (CharSequence) null);

            switch (result) {
                case NativeFileDialog.NFD_OKAY:
                    FileHandle file = new FileHandle(path.getStringUTF8(0));
                    callback.onFileChosen(file);
                    NFD_FreePath(path.get(0));
                    break;
                case NativeFileDialog.NFD_CANCEL:
                    callback.onCancellation();
                    break;
                case NativeFileDialog.NFD_ERROR:
                    callback.onError(new Exception(NFD_GetError()));
                    break;
            }
        } catch (Exception e) {
            callback.onError(e);
        } finally {
            memFree(path);
        }
    }
}

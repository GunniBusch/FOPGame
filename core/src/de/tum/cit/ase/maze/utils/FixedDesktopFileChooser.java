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
     * Chooses a file using the provided configuration and asynchronous callback.
     *
     * @param configuration File choosing configuration, must not be null.
     *                      <h4> ATTENTION: </h4> <p>
     *                      {@link NativeFileChooserConfiguration#mimeFilter} will be used ass follow to filter name: {@code "Simplename/extension,extension;..."}
     *                      </p>
     *                      <p>
     *                      {@link NativeFileChooserConfiguration#title} will be use as the default file name when {@link NativeFileChooserConfiguration#intent} is set to {@link NativeFileChooserIntent#SAVE SAVE}
     *
     *                      </p>
     * @param callback      File choosing asynchronous callback, must not be null
     * @see NativeFileDialog
     * @see NFDFilterItem
     * @see NativeFileChooser
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
            int result = configuration.intent == NativeFileChooserIntent.SAVE ?
                    NativeFileDialog.NFD_SaveDialog(path, filterList, configuration.directory.file().getPath(), configuration.title) :
                    NativeFileDialog.NFD_OpenDialog(path, filterList, configuration.directory.file().getPath());

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

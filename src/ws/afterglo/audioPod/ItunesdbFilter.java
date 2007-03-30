package ws.afterglo.audioPod;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * @author Muti
 *
 */
public class ItunesdbFilter extends FileFilter {
    public boolean accept(File f) {
        if(f.isDirectory())
            return true;
        
        if(f.getName().equals("iTunesDB")) {
            return true;
        }
        
        return false;
    }
    
    public String getDescription() {
        return "iTunesDB Files";
    }
}
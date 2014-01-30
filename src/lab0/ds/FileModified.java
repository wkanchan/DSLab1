package lab0.ds;

import java.io.File;
import java.util.TimerTask;

public abstract class FileModified extends TimerTask{
	private long time;
	private File file;

	public FileModified( File file ) {
		this.file = file;
		this.time = file.lastModified();
	}

	@Override
	public void run() {
		long time = file.lastModified();

		if( this.time != time ) {
			this.time = time;
			onChange(file);
		}
	}
	protected abstract void onChange( File file );
}

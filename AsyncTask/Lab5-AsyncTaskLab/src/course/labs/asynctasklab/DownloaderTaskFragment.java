package course.labs.asynctasklab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

public class DownloaderTaskFragment<DownLoaderTask> extends Fragment {

	private DownloadFinishedListener mCallback;
	private Context mContext;
	
	@SuppressWarnings ("unused")
	private static final String TAG = "Lab-Threads";
	static final String fragmentName = "friends";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Preserve across reconfigurations
		setRetainInstance(true);
		
		// TODO: Create new DownloaderTask that "downloads" data
		DownLoaderTask downLoaderTask = new DownLoaderTask();
		
		// TODO: Retrieve arguments from DownloaderTaskFragment
		// Prepare them for use with DownloaderTask. 
		Bundle args = getArguments();
		
		//get the list of names from the FriendsFragment that makes up the UI
		ArrayList<Integer> friendsNames = args.getIntegerArrayList(fragmentName);
        
        //Create integer array for size of friendsNames 
        Integer[] mResourceIds = new Integer[friendsNames.size()];

        //Initialize mResourceIds with values from friendsNames
        //friendsNames must be cast to get Integer[], otherwise Object[] is returned
        mResourceIds = (Integer[]) friendsNames.toArray(mResourceIds);

        // TODO: Start the DownloaderTask 
        downLoaderTask.execute(mResourceIds);
	}

	// Assign current hosting Activity to mCallback
	// Store application context for use by downloadTweets()
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		mContext = activity.getApplicationContext(); 

		// Make sure that the hosting activity has implemented
		// the correct callback interface.
		try {
			mCallback = (DownloadFinishedListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString()
					+ " must implement DownloadFinishedListener");
		}
	}

	// Null out mCallback
	@Override
	public void onDetach() {
		super.onDetach();
		mCallback = null;
	}

	// TODO: Implement an AsyncTask subclass called DownLoaderTask.
	// This class must use the downloadTweets method (currently commented
	// out). Ultimately, it must also pass newly available data back to 
	// the hosting Activity using the DownloadFinishedListener interface.

	public class DownLoaderTask extends AsyncTask<Integer[], Void, String[]> {
		
        // TODO: Uncomment this helper method
		// Simulates downloading Twitter data from the network      
         private String[] downloadTweets(Integer resourceIDS[]) {
        	 
			final int simulatedDelay = 2000;
			String[] feeds = new String[resourceIDS.length];
			try {
				for (int idx = 0; idx < resourceIDS.length; idx++) {
					InputStream inputStream;
					BufferedReader in;
					try {
						// Pretend downloading takes a long time
						Thread.sleep(simulatedDelay);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					inputStream = mContext.getResources().openRawResource(
							resourceIDS[idx]);
					in = new BufferedReader(new InputStreamReader(inputStream));

					String readLine;
					StringBuffer buf = new StringBuffer();

					while ((readLine = in.readLine()) != null) {
						buf.append(readLine);
					}

					feeds[idx] = buf.toString();

					if (null != in) {
						in.close();
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return feeds;
		}

		@Override
		protected String[] doInBackground(Integer[]... params) {
		String[] feeds = downloadTweets(params[0]);
           return feeds;

		}
		
		//Pass data back to hosting Activity
        protected void onPostExecute(String[] feeds) {
            mCallback.notifyDataRefreshed(feeds);
        }
	}
}
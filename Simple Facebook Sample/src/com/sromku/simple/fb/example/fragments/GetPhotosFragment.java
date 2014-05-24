package com.sromku.simple.fb.example.fragments;

import java.util.List;

import android.graphics.Paint;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.actions.Cursor;
import com.sromku.simple.fb.entities.Photo;
import com.sromku.simple.fb.example.R;
import com.sromku.simple.fb.listeners.OnPhotosListener;
import com.sromku.simple.fb.utils.Utils;

public class GetPhotosFragment extends BaseFragment{

	private TextView mResult;
	private Button mGetButton;
	private TextView mMore;
	private String mAllPages = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActivity().setTitle("Get photos");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_get_albums, container, false);
		mResult = (TextView) view.findViewById(R.id.result);
		mMore = (TextView) view.findViewById(R.id.load_more);
		mMore.setPaintFlags(mMore.getPaint().getFlags() | Paint.UNDERLINE_TEXT_FLAG);
		mGetButton = (Button) view.findViewById(R.id.button);
		mGetButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mAllPages = "";
				mResult.setText(mAllPages);

				SimpleFacebook.getInstance().getPhotos(new OnPhotosListener() {

					@Override
					public void onThinking() {
						showDialog();
					}

					@Override
					public void onException(Throwable throwable) {
						hideDialog();
						mResult.setText(throwable.getMessage());
					}

					@Override
					public void onFail(String reason) {
						hideDialog();
						mResult.setText(reason);
					}

					@Override
					public void onComplete(List<Photo> response) {
						hideDialog();
						// make the result more readable. 
						mAllPages += "<u>\u25B7\u25B7\u25B7 (paging) #" + getPageNum() + " \u25C1\u25C1\u25C1</u><br>";
						mAllPages += Utils.join(response.iterator(), "<br>", new Utils.Process<Photo>() {
							@Override
							public String process(Photo photo) {
								return "\u25CF " + photo.getSource() + " \u25CF";
							}
						});
						mAllPages += "<br>";
						mResult.setText(Html.fromHtml(mAllPages));

						// check if more pages exist
						if (hasNext()) {
							enableLoadMore(getCursor());
						} else {
							disableLoadMore();
						}
					}
				});
			}
		});
		return view;
	}

	private void enableLoadMore(final Cursor<List<Photo>> cursor) {
		mMore.setVisibility(View.VISIBLE);
		mMore.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				mAllPages += "<br>";
				cursor.next();
			}
		});
	}

	private void disableLoadMore() {
		mMore.setOnClickListener(null);
		mMore.setVisibility(View.INVISIBLE);
	}
}

package io.supportkit.ethanifier.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import io.supportkit.ethanifier.R;
import io.supportkit.ethanifier.utils.ImageHelper;
import io.supportkit.ui.ConversationActivity;

public class LoggedInFragment extends Fragment implements View.OnClickListener {
    private static Button chatButton;
    private static ImageView avatarImage;
    private static ImageView presenceImage;
    private static TextView slackUser;
    private static TextView presenceText;

    private String presence, name, avatarUrl;
    private boolean slackResponseSuccessful = false;
    private Resources resources;
    private Bitmap anonymousAvatar;

    public void onSlackResponse(String presence, String name, String avatarUrl) {
        this.presence = presence;
        this.name = name;
        this.avatarUrl = avatarUrl;

        slackResponseSuccessful = true;

        downloadAvatar();
    }

    private void downloadAvatar() {
        final Context activity = getActivity();

        if (activity != null) {
            Picasso.with(getActivity()).load(avatarUrl).into(target);
            slackUser.setText(name);

            switch (presence) {
                case "active":
                    presenceImage.setImageResource(R.drawable.green_circle);
                    presenceText.setText("Online");
                    break;
                case "away":
                    presenceImage.setImageResource(R.drawable.orange_circle);
                    presenceText.setText("Away");
                    break;
                case "offline":
                    presenceImage.setImageResource(R.drawable.gray_circle);
                    presenceText.setText("Offline");
                    break;
                default:
                    presenceImage.setImageResource(R.drawable.gray_circle);
                    presenceText.setText("Offline");
            }
        }
    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            avatarImage.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bitmap, resources.getDimensionPixelSize(R.dimen.Ethanifier_avatarSize)));
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        resources = getResources();
        anonymousAvatar = BitmapFactory.decodeResource(resources, io.supportkit.ui.R.drawable.supportkit_img_avatar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_logged_in, container, false);

        chatButton = (Button) view.findViewById(R.id.chatButton);
        avatarImage = (ImageView) view.findViewById(R.id.avatarImage);
        presenceImage = (ImageView) view.findViewById(R.id.presenceImage);
        slackUser = (TextView) view.findViewById(R.id.slackUser);
        presenceText = (TextView) view.findViewById(R.id.presenceText);

        avatarImage.setImageBitmap(ImageHelper.getRoundedCornerBitmap(anonymousAvatar, resources.getDimensionPixelSize(R.dimen.Ethanifier_avatarSize)));

        chatButton.setOnClickListener(this);

        if (slackResponseSuccessful) {
            downloadAvatar();
        }

        return view;
    }

    @Override
    public void onClick(View v) {
        final int id = v.getId();

        if (id == R.id.chatButton) {
            ConversationActivity.show(getActivity());
        }
    }
}

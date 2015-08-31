package io.supportkit.ethanifier.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.supportkit.ethanifier.R;
import io.supportkit.ethanifier.utils.ImageHelper;
import io.supportkit.ui.ConversationActivity;

public class LoggedInFragment extends Fragment implements View.OnClickListener {
    private static Button chatButton;
    private static ImageView avatarImage;
    private static ImageView presenceImage;
    private static TextView slackUserText;
    private static TextView presenceText;
    private static ProgressBar loadingSpinner;

    private static TextView loadingText;

    private String presence, name;
    private Resources resources;
    private Bitmap anonymousAvatarBitmap;
    private Bitmap avatarBitmap;

    public void onPresenceLoaded(String presence, String name) {
        this.presence = presence;
        this.name = name;

        updateAvailability();
    }

    public void onAvatarLoaded(Bitmap avatar) {
        this.avatarBitmap = avatar;

        updateAvatar();
    }

    private void updateAvailability() {
        final Context activity = getActivity();

        if (activity != null) {
            slackUserText.setText(name);

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

            avatarImage.setVisibility(View.VISIBLE);
            presenceImage.setVisibility(View.VISIBLE);
            slackUserText.setVisibility(View.VISIBLE);
            presenceText.setVisibility(View.VISIBLE);
            loadingSpinner.setVisibility(View.GONE);
            loadingText.setVisibility(View.GONE);
        }
    }

    private void updateAvatar() {
        final Context activity = getActivity();

        if(activity != null) {
            avatarImage.setImageBitmap(ImageHelper.getRoundedCornerBitmap(avatarBitmap, resources.getDimensionPixelSize(R.dimen.Ethanifier_avatarSize)));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        resources = getResources();
        anonymousAvatarBitmap = BitmapFactory.decodeResource(resources, io.supportkit.ui.R.drawable.supportkit_img_avatar);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_logged_in, container, false);

        chatButton = (Button) view.findViewById(R.id.chatButton);
        avatarImage = (ImageView) view.findViewById(R.id.avatarImage);
        presenceImage = (ImageView) view.findViewById(R.id.presenceImage);
        slackUserText = (TextView) view.findViewById(R.id.slackUserText);
        presenceText = (TextView) view.findViewById(R.id.presenceText);
        loadingText = (TextView) view.findViewById(R.id.loadingText);
        loadingSpinner = (ProgressBar) view.findViewById(R.id.loadingSpinner);

        avatarImage.setImageBitmap(ImageHelper.getRoundedCornerBitmap(anonymousAvatarBitmap, resources.getDimensionPixelSize(R.dimen.Ethanifier_avatarSize)));

        chatButton.setOnClickListener(this);

        if (presence != null) {
            updateAvailability();
        }

        if (avatarBitmap != null) {
            updateAvatar();
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

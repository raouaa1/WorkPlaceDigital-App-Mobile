package pi.tn.workplacedigital_mobile.activities;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import pi.tn.workplacedigital_mobile.R;

public class MediaFullscreenActivity extends AppCompatActivity {

    public static final String EXTRA_MEDIA_URL = "media_url";
    public static final String EXTRA_IS_VIDEO = "is_video";

    private ImageView fullscreenImageView;
    private VideoView fullscreenVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_fullscreen);

        fullscreenImageView = findViewById(R.id.fullscreenImageView);
        fullscreenVideoView = findViewById(R.id.fullscreenVideoView);

        String mediaUrl = getIntent().getStringExtra(EXTRA_MEDIA_URL);
        boolean isVideo = getIntent().getBooleanExtra(EXTRA_IS_VIDEO, false);

        if (isVideo) {
            fullscreenImageView.setVisibility(ImageView.GONE);
            fullscreenVideoView.setVisibility(VideoView.VISIBLE);

            fullscreenVideoView.setVideoURI(Uri.parse(mediaUrl));
            fullscreenVideoView.start();

            // Optionnel : boucle vidéo
            fullscreenVideoView.setOnCompletionListener(mp -> fullscreenVideoView.start());
        } else {
            fullscreenVideoView.setVisibility(VideoView.GONE);
            fullscreenImageView.setVisibility(ImageView.VISIBLE);

            Glide.with(this)
                    .load(mediaUrl)
                    .error(R.drawable.logoooredoo)
                    .into(fullscreenImageView);
        }
    }
}

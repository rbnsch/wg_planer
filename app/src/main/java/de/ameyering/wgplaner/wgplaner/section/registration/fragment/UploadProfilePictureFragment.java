package de.ameyering.wgplaner.wgplaner.section.registration.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import java.io.IOException;
import java.util.Random;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.WGPlanerApplication;
import de.ameyering.wgplaner.wgplaner.customview.CircularImageView;
import de.ameyering.wgplaner.wgplaner.utils.DataProvider;
import de.ameyering.wgplaner.wgplaner.utils.DataProviderInterface;

public class UploadProfilePictureFragment extends NavigationFragment {
    public static final int REQ_CODE_PICK_IMAGE = 0;
    private static int standard_width = 512;
    private static int standard_text_size = 300;
    private CircularImageView image;
    private Bitmap bitmap;
    private Uri selectedImage;
    private DataProviderInterface dataProvider;

    private StateEMailFragment stateEMailFragment;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
        @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upload_profile_picture_registration, container,
                false);

        WGPlanerApplication application = (WGPlanerApplication) getActivity().getApplication();
        dataProvider = application.getDataProviderInterface();

        if (stateEMailFragment == null) {
            stateEMailFragment = new StateEMailFragment();
        }

        image = view.findViewById(R.id.registration_profile_picture);
        image.addOnRotationListener(new CircularImageView.OnRotationListener() {
            @Override
            public void onRotateLeft(Bitmap bitmap) {
                image.setImageBitmap(bitmap);
            }

            @Override
            public void onRotateRight(Bitmap bitmap) {
                image.setImageBitmap(bitmap);
            }
        });
        image.setOnClickListener(view1 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            CharSequence[] options = new CharSequence[2];
            options[0] = getString(R.string.pick_image);
            options[1] = getString(R.string.generate_image);
            builder.setItems(options, (dialogInterface, i) -> {
                if (i == 0) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQ_CODE_PICK_IMAGE);

                } else if (i == 1) {
                    bitmap = createStandardBitmap(dataProvider.getCurrentUserDisplayName());

                    getActivity().runOnUiThread(() -> {
                        image.setImageBitmap(bitmap);
                        image.startAnimation(AnimationUtils.loadAnimation(getContext(),
                                R.anim.anim_load_new_profile_picture));
                    });
                }
            });

            builder.show();
        });

        bitmap = dataProvider.getCurrentUserImage();

        if (bitmap == null) {
            bitmap = createStandardBitmap(dataProvider.getCurrentUserDisplayName());
        }

        image.setImageBitmap(bitmap);

        Button buttonContinue = view.findViewById(R.id.btn_continue_upload_profile_picture);

        buttonContinue.setOnClickListener(view12 -> {
            dataProvider.setCurrentUserImage(bitmap, null);
            loadNext();
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_PICK_IMAGE:
                if (resultCode == AppCompatActivity.RESULT_OK) {

                    selectedImage = data.getData();

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImage);
                        bitmap = scaleBitmap(bitmap);
                        image.setImageBitmap(bitmap);
                        image.startAnimation(AnimationUtils.loadAnimation(getContext(),
                                R.anim.anim_load_new_profile_picture));

                    } catch (IOException e) {
                        Toast.makeText(getContext(), getString(R.string.load_profile_picture_error),
                            Toast.LENGTH_LONG).show();
                    }
                }

                break;
        }
    }

    private void loadNext() {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_fragment_enter_from_right,
            R.anim.anim_fragment_exit_to_left, R.anim.anim_fragment_enter_from_left,
            R.anim.anim_fragment_exit_to_right);
        transaction.hide(UploadProfilePictureFragment.this);
        transaction.add(R.id.container_registration, stateEMailFragment);
        transaction.addToBackStack("");
        transaction.commit();
    }

    private Bitmap scaleBitmap(Bitmap bitmap) {
        int maxLength = Math.max(bitmap.getHeight(), bitmap.getWidth());
        float scale = (float) 800 / (float) maxLength;

        int newWidth = Math.round(bitmap.getWidth() * scale);
        int newHeight = Math.round(bitmap.getHeight() * scale);

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true);
    }

    private Bitmap createStandardBitmap(String displayName) {
        Bitmap standard = Bitmap.createBitmap(standard_width, standard_width, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(standard);
        Paint paint = new Paint();

        Random random = new Random();
        int randomRed = random.nextInt(230);
        int randomGreen = random.nextInt(230);
        int randomBlue = random.nextInt(230);

        int color = Color.argb(255, randomRed, randomGreen, randomBlue);

        paint.setColor(color);

        canvas.drawRect(0, 0, standard_width, standard_width, paint);


        Paint textPaint = new Paint();
        textPaint.setARGB(255, 255, 255, 255);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(standard_text_size);

        int xPos = (canvas.getWidth() / 2);
        int yPos = (int)((canvas.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2));

        canvas.drawText(displayName.substring(0, 1), xPos, yPos, textPaint);

        return standard;
    }
}

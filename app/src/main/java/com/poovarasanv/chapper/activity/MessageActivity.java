package com.poovarasanv.chapper.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.poovarasanv.chapper.R;
import com.poovarasanv.chapper.databinding.ActivityMessageBinding;
import com.poovarasanv.chapper.singleton.ChapperSingleton;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.emoji.Emoji;
import com.vanniktech.emoji.listeners.OnEmojiBackspaceClickListener;
import com.vanniktech.emoji.listeners.OnEmojiClickedListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardCloseListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardOpenListener;

import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;

public class MessageActivity extends AppCompatActivity {


    ActivityMessageBinding activityMessageBinding;
    SupportAnimator animator_reverse;
    boolean hidden = true;
    SupportAnimator animator;
    int cx, cy, radius;

    private EmojiPopup emojiPopup;
    public static boolean isVoice = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMessageBinding = DataBindingUtil.setContentView(this, R.layout.activity_message);

        String phoneNumber = getIntent().getStringExtra("phonenumber");

        setSupportActionBar(activityMessageBinding.toolbar);

        activityMessageBinding.toolBarTitle.setText("Poovarasan Aircel");
        activityMessageBinding.toolBarSubtitle.setText("Online");
        getSupportActionBar().setTitle("");


        activityMessageBinding.reveal.setVisibility(View.INVISIBLE);


        activityMessageBinding.messageText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length() > 0) {
                    activityMessageBinding.cameraBtn.setVisibility(View.GONE);
                    isVoice = false;
                    activityMessageBinding.sendBtn.setImageResource(R.drawable.ic_send);
                } else {
                    activityMessageBinding.cameraBtn.setVisibility(View.VISIBLE);
                    isVoice = true;
                    activityMessageBinding.sendBtn.setImageResource(R.drawable.ic_mic);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        activityMessageBinding.cameraBtn.setOnClickListener(view -> {
            ActivityOptionsCompat options =
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                            this, view, "DetailActivity:image");
            Intent intent = new Intent(this, CameraActivity.class);
            ActivityCompat.startActivity(this, intent, options.toBundle());

        });
        activityMessageBinding.toolBarBackBtn.setOnClickListener(view -> {
            finish();
        });
        activityMessageBinding.fullFrame.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (animator != null) {
                    animator_reverse = animator.reverse();
                    Log.i("Touch Called", "Touch");

                    if (hidden == false) {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            animator_reverse.addListener(new SupportAnimator.AnimatorListener() {
                                @Override
                                public void onAnimationStart() {

                                }

                                @Override
                                public void onAnimationEnd() {
                                    activityMessageBinding.reveal.setVisibility(View.INVISIBLE);
                                    hidden = true;

                                }

                                @Override
                                public void onAnimationCancel() {

                                }

                                @Override
                                public void onAnimationRepeat() {

                                }
                            });
                            animator_reverse.start();
                        } else {
                            Animator anim = android.view.ViewAnimationUtils.createCircularReveal(activityMessageBinding.reveal, cx, cy, radius, 0);
                            anim.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    super.onAnimationEnd(animation);
                                    activityMessageBinding.reveal.setVisibility(View.INVISIBLE);
                                    hidden = true;
                                }
                            });
                            anim.start();
                        }
                    }
                }
                return false;
            }
        });
        activityMessageBinding.toolbarProfileImage.setImageDrawable(getResources().getDrawable(R.drawable.default_image));
        activityMessageBinding.emoji.setOnClickListener(view -> emojiPopup.toggle());
        setUpEmojiPopup();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.message_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }


    private void setUpEmojiPopup() {
        emojiPopup = EmojiPopup.Builder.fromRootView(activityMessageBinding.fullFrame).setOnEmojiBackspaceClickListener(new OnEmojiBackspaceClickListener() {
            @Override
            public void onEmojiBackspaceClicked(final View v) {
                Log.d("MainActivity", "Clicked on Backspace");
            }
        }).setOnEmojiClickedListener(new OnEmojiClickedListener() {
            @Override
            public void onEmojiClicked(final Emoji emoji) {
                Log.d("MainActivity", "Clicked on emoji");
            }
        }).setOnEmojiPopupShownListener(new OnEmojiPopupShownListener() {
            @Override
            public void onEmojiPopupShown() {
                activityMessageBinding.emoji.setImageResource(R.drawable.ic_keyboard);
            }
        }).setOnSoftKeyboardOpenListener(new OnSoftKeyboardOpenListener() {
            @Override
            public void onKeyboardOpen(final int keyBoardHeight) {
                Log.d("MainActivity", "Opened soft keyboard");
            }
        }).setOnEmojiPopupDismissListener(new OnEmojiPopupDismissListener() {
            @Override
            public void onEmojiPopupDismiss() {
                activityMessageBinding.emoji.setImageResource(R.drawable.emoji_people);
            }
        }).setOnSoftKeyboardCloseListener(new OnSoftKeyboardCloseListener() {
            @Override
            public void onKeyboardClose() {
                emojiPopup.dismiss();
            }
        }).build(activityMessageBinding.messageText);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_clip: {

                cx = (activityMessageBinding.reveal.getLeft() + activityMessageBinding.reveal.getRight());
                cy = activityMessageBinding.reveal.getTop();
                radius = Math.max(activityMessageBinding.reveal.getWidth(), activityMessageBinding.reveal.getHeight());

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {


                    animator =
                            ViewAnimationUtils.createCircularReveal(activityMessageBinding.reveal, cx, cy, 0, radius);
                    animator.setInterpolator(new AccelerateDecelerateInterpolator());
                    animator.setDuration(200);


                    if (hidden) {
                        activityMessageBinding.reveal.setVisibility(View.VISIBLE);
                        animator.start();
                        hidden = false;
                    } else {
                        animator_reverse = animator.reverse();
                        animator_reverse.addListener(new SupportAnimator.AnimatorListener() {
                            @Override
                            public void onAnimationStart() {

                            }

                            @Override
                            public void onAnimationEnd() {
                                activityMessageBinding.reveal.setVisibility(View.INVISIBLE);
                                hidden = true;

                            }

                            @Override
                            public void onAnimationCancel() {

                            }

                            @Override
                            public void onAnimationRepeat() {

                            }
                        });
                        animator_reverse.start();

                    }
                } else {
                    if (hidden) {
                        Animator anim = android.view.ViewAnimationUtils.createCircularReveal(activityMessageBinding.reveal, cx, cy, 0, radius);
                        activityMessageBinding.reveal.setVisibility(View.VISIBLE);
                        anim.start();
                        hidden = false;

                    } else {
                        Animator anim = android.view.ViewAnimationUtils.createCircularReveal(activityMessageBinding.reveal, cx, cy, radius, 0);
                        anim.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                activityMessageBinding.reveal.setVisibility(View.INVISIBLE);
                                hidden = true;
                            }
                        });
                        anim.start();
                    }
                }

                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (emojiPopup != null && emojiPopup.isShowing()) {
            emojiPopup.dismiss();
        } else {
            finish();
            super.onBackPressed();
        }
    }

}

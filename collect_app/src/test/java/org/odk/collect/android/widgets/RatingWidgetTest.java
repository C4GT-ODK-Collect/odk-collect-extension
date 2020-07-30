package org.odk.collect.android.widgets;

import android.view.View;

import org.javarosa.core.model.RangeQuestion;
import org.javarosa.core.model.data.StringData;
import org.javarosa.form.api.FormEntryPrompt;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.odk.collect.android.formentry.questions.QuestionDetails;
import org.odk.collect.android.listeners.WidgetValueChangedListener;
import org.robolectric.RobolectricTestRunner;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.odk.collect.android.widgets.support.QuestionWidgetHelpers.mockValueChangedListener;

import static org.odk.collect.android.widgets.support.QuestionWidgetHelpers.promptWithQuestionAndAnswer;
import static org.odk.collect.android.widgets.support.QuestionWidgetHelpers.promptWithReadOnlyAndQuestion;
import static org.odk.collect.android.widgets.support.QuestionWidgetHelpers.widgetTestActivity;

@RunWith(RobolectricTestRunner.class)
public class RatingWidgetTest {

    private RangeQuestion rangeQuestion;

    @Before
    public void setup() {
        rangeQuestion = mock(RangeQuestion.class);
        when(rangeQuestion.getRangeEnd()).thenReturn(BigDecimal.valueOf(5));
    }

    @Test
    public void usingReadOnly_makesAllClickableElementsDisabled() {
        RatingWidget widget = createWidget(promptWithReadOnlyAndQuestion(rangeQuestion));

        assertThat(widget.binding.ratingBar1.isEnabled(), equalTo(false));
        assertThat(widget.binding.ratingBar2.isEnabled(), equalTo(false));
    }

    @Test
    public void ratingWidgetShowsCorrectViewForLessNumberOfStars() {
        when(rangeQuestion.getRangeEnd()).thenReturn(BigDecimal.valueOf(4));
        RatingWidget widget = createWidget(promptWithReadOnlyAndQuestion(rangeQuestion));

        assertThat(widget.binding.ratingBar1.getNumStars(), equalTo(4));
        assertThat(widget.binding.ratingBar1.getMax(), equalTo(4));
        assertThat(widget.binding.ratingBar2.getVisibility(), equalTo(View.GONE));
    }

    @Test
    public void ratingWidgetShowsCorrectViewForMoreNumberOfStars() {
        when(rangeQuestion.getRangeEnd()).thenReturn(BigDecimal.valueOf(8));
        RatingWidget widget = createWidget(promptWithReadOnlyAndQuestion(rangeQuestion));

        assertThat(widget.binding.ratingBar1.getNumStars(), equalTo(5));
        assertThat(widget.binding.ratingBar1.getMax(), equalTo(5));
        assertThat(widget.binding.ratingBar2.getVisibility(), equalTo(View.VISIBLE));
        assertThat(widget.binding.ratingBar2.getNumStars(), equalTo(3));
        assertThat(widget.binding.ratingBar2.getMax(), equalTo(3));
    }

    @Test
    public void getAnswer_whenPromptAnswerDoesNotHaveAnswer_returnsNull() {
        assertThat(createWidget(promptWithReadOnlyAndQuestion(rangeQuestion)).getAnswer(), nullValue());
    }

    @Test
    public void getAnswer_whenPromptHasAnswer_returnsAnswer_forRatingBarInSingleLine() {
        RatingWidget widget = createWidget(promptWithQuestionAndAnswer(rangeQuestion, new StringData("3")));
        assertThat(widget.getAnswer().getValue(), equalTo(3));
    }

    @Test
    public void getAnswer_whenPromptHasAnswer_returnsAnswer_forRatingBarInMultipleLines() {
        when(rangeQuestion.getRangeEnd()).thenReturn(BigDecimal.valueOf(10));
        RatingWidget widget = createWidget(promptWithQuestionAndAnswer(rangeQuestion, new StringData("7")));

        assertThat(widget.getAnswer().getValue(), equalTo(7));
    }

    @Test
    public void settingRatingOnTopRatingBar_deselectsAllStarsOnBottomRatingBar() {
        when(rangeQuestion.getRangeEnd()).thenReturn(BigDecimal.valueOf(10));
        RatingWidget widget = createWidget(promptWithQuestionAndAnswer(rangeQuestion, null));
        widget.binding.ratingBar1.setRating(4.0F);

        assertThat(widget.binding.ratingBar2.getRating(), equalTo(0.0F));
    }

    @Test
    public void settingRatingOnBottomRatingBar_selectsAllStarsOnTopRatingBar() {
        when(rangeQuestion.getRangeEnd()).thenReturn(BigDecimal.valueOf(10));
        RatingWidget widget = createWidget(promptWithQuestionAndAnswer(rangeQuestion, null));
        widget.binding.ratingBar2.setRating(4.0F);

        assertThat(widget.binding.ratingBar1.getRating(), equalTo(5.0F));
    }

    @Test
    public void whenPromptDoesNotHaveAnswer_noStarsAreHighlightedOnRatingBar() {
        RatingWidget widget = createWidget(promptWithQuestionAndAnswer(rangeQuestion, null));
        assertThat(widget.binding.ratingBar1.getRating(), equalTo(0.0F));
    }

    @Test
    public void whenPromptHasAnswer_correctNumberOfStarsAreHighlighted_forSmallerRatingBar() {
        RatingWidget widget = createWidget(promptWithQuestionAndAnswer(rangeQuestion, new StringData("3")));
        assertThat(widget.binding.ratingBar1.getRating(), equalTo(3.0F));
    }

    @Test
    public void whenPromptHasAnswer_correctNumberOfStarsAreHighlighted_forRatingBarInMultipleLines() {
        when(rangeQuestion.getRangeEnd()).thenReturn(BigDecimal.valueOf(10));
        RatingWidget widget = createWidget(promptWithQuestionAndAnswer(rangeQuestion, new StringData("7")));
        assertThat(widget.binding.ratingBar2.getRating(), equalTo(2.0F));
    }

    @Test
    public void clearAnswer_clearsWidgetAnswer() {
        RatingWidget widget = createWidget(promptWithQuestionAndAnswer(rangeQuestion, new StringData("3")));
        widget.clearAnswer();
        assertThat(widget.binding.ratingBar1.getRating(), equalTo(0.0F));
    }

    @Test
    public void clearAnswer_callsValueChangeListeners() {
        RatingWidget widget = createWidget(promptWithQuestionAndAnswer(rangeQuestion, new StringData("3")));
        WidgetValueChangedListener valueChangedListener = mockValueChangedListener(widget);
        widget.setValueChangedListener(valueChangedListener);
        widget.clearAnswer();

        verify(valueChangedListener).widgetValueChanged(widget);
    }

    @Test
    public void changingRating_callsValueChangeListeners_forRatingBarInSingleLine() {
        RatingWidget widget = createWidget(promptWithQuestionAndAnswer(rangeQuestion, null));
        WidgetValueChangedListener valueChangedListener = mockValueChangedListener(widget);
        widget.setValueChangedListener(valueChangedListener);
        widget.binding.ratingBar1.setRating(4.0F);

        verify(valueChangedListener).widgetValueChanged(widget);
    }

    @Test
    public void changingRating_callsValueChangeListeners_forRatingBarInMultipleLines() {
        when(rangeQuestion.getRangeEnd()).thenReturn(BigDecimal.valueOf(10));
        RatingWidget widget = createWidget(promptWithQuestionAndAnswer(rangeQuestion, null));
        WidgetValueChangedListener valueChangedListener = mockValueChangedListener(widget);
        widget.setValueChangedListener(valueChangedListener);
        widget.binding.ratingBar2.setRating(4.0F);

        verify(valueChangedListener).widgetValueChanged(widget);
    }

    @Test
    public void ratingBar_doesNotAllowUserToSetDecimalRating_forRatingBarInSingleLine() {
        RatingWidget widget = createWidget(promptWithQuestionAndAnswer(rangeQuestion, null));
        widget.binding.ratingBar1.setRating(1.8F);
        assertThat(widget.binding.ratingBar1.getRating(), equalTo(2.0F));
    }

    @Test
    public void ratingBar_doesNotAllowUserToSetDecimalRating_forRatingBarInMultipleLines() {
        when(rangeQuestion.getRangeEnd()).thenReturn(BigDecimal.valueOf(10));
        RatingWidget widget = createWidget(promptWithQuestionAndAnswer(rangeQuestion, null));
        widget.binding.ratingBar2.setRating(1.8F);

        assertThat(widget.binding.ratingBar2.getRating(), equalTo(2.0F));
    }

    @Test
    public void clickingRatingBarForLong_callsLongClickListener() {
        View.OnLongClickListener listener = mock(View.OnLongClickListener.class);
        RatingWidget widget = createWidget(promptWithQuestionAndAnswer(rangeQuestion, null));
        widget.setOnLongClickListener(listener);
        widget.binding.ratingBar1.performLongClick();
        widget.binding.ratingBar2.performLongClick();

        verify(listener).onLongClick(widget.binding.ratingBar1);
        verify(listener).onLongClick(widget.binding.ratingBar2);
    }

    private RatingWidget createWidget(FormEntryPrompt prompt) {
        return new RatingWidget(widgetTestActivity(), new QuestionDetails(prompt, "formAnalyticsID"));
    }
}

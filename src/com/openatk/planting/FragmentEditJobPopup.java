package com.openatk.planting;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.openatk.planting.R;
import com.openatk.planting.db.DatabaseHelper;
import com.openatk.planting.db.Field;
import com.openatk.planting.db.Job;
import com.openatk.planting.db.Seed;
import com.openatk.planting.db.TableOperations;
import com.openatk.planting.db.TableWorkers;
import com.openatk.planting.db.TableSeed;
import com.openatk.planting.db.Worker;
import com.openatk.planting.db.Note;
import com.openatk.planting.db.TableNotes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class FragmentEditJobPopup extends Fragment implements
		OnCheckedChangeListener, OnClickListener, OnItemSelectedListener, OnTouchListener {

	EditJobListener listener;
	private Field currentField = null;

	private Job currentJob = null;
	
	private TextView tvName;
	private TextView tvAcres;
	private ImageButton butEditField;

	private CheckBox chkPlanned;
	private CheckBox chkStarted;
	private CheckBox chkDone;

	private TextView tvCalendar;
	private ImageButton butCalendar;

	private DatabaseHelper dbHelper;
	private List<Worker> workerList = null;
	private List<Seed> seedList = null;
	private ArrayAdapter<Worker> spinWorkerAdapter = null;
	private Spinner spinWorker;
	private Button butNewWorker;
	private ArrayAdapter<Seed> spinSeedAdapter = null;
	private Spinner spinSeed;
	private Button butNewSeed;
	private View layout;

	private ImageButton AddSubNote;
	private List<Note> notes;
	private LinearLayout list_notes;
	private Note currentNote = null;
	private Seed seed;
	private Seed seedim = null;
	LayoutInflater vi;
	
	private ImageButton butDone;
	private EditText etComment;
	private EditText etSeednote;
	private EditText SeedInfo;
	//private EditText fieldnote1; //field note title 1
	//private EditText fieldnoteCon1; //field note content 1
	//private EditText fieldnote2;  //field note title 2
	//private EditText fieldnoteCon2; //field note content 2
	private ImageButton butDelete;
	private ImageButton CameraButton;
	private ImageButton CameraResult;
	private boolean pictureTaken = false;
	private String imageSeedName;
	public File image;
	private Seed seedObjectImage;
	//private String imageLoc;

	public File getImage() {
		return image;
	}

	public void setImage(File image) {
		this.image = image;
	}
	private Button replace;
	private Button exit;
	private AlertDialog imageD;
	// Interface for receiving data
	public interface EditJobListener {
		
		public void EditJobSave(Job job);

		public void EditJobSave(Job job, Boolean changeState, Boolean unselect);

		public void EditJobDelete();

		public Field EditJobGetCurrentField();

		public Job EditJobGetCurrentJob();

		public void EditJobDateSave(int year, int month, int day);

		public void EditJobEditField();
		
		public void SliderDragDown(int start);
		public void SliderDragDragging(int whereY);
		public void SliderDragUp(int whereY);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_slider,
				container, false);

		dbHelper = new DatabaseHelper(this.getActivity());

		tvName = (TextView) view.findViewById(R.id.edit_field_tvName);
		tvAcres = (TextView) view.findViewById(R.id.edit_field_tvAcres);
		butEditField = (ImageButton) view
				.findViewById(R.id.edit_field_butEditField);
		
		view.setOnTouchListener(this);
		tvName.setOnTouchListener(this);
		tvAcres.setOnTouchListener(this);
		
		chkPlanned = (CheckBox) view.findViewById(R.id.edit_field_chkPlanned);
		chkStarted = (CheckBox) view.findViewById(R.id.edit_field_chkStarted);
		chkDone = (CheckBox) view.findViewById(R.id.edit_field_chkDone);

		tvCalendar = (TextView) view.findViewById(R.id.edit_field_tvCalendar);
		butCalendar = (ImageButton) view
				.findViewById(R.id.edit_field_butCalendar);
		spinWorker = (Spinner) view.findViewById(R.id.edit_field_spinOperator);
		butNewWorker = (Button) view
				.findViewById(R.id.edit_field_butNewOperator);
		spinSeed = (Spinner) view.findViewById(R.id.edit_field_spinSeed);
		butNewSeed = (Button) view
				.findViewById(R.id.edit_field_butNewSeed);
		etSeednote = (EditText) view.findViewById(R.id.Seed_Job_Note);
		SeedInfo = (EditText) view.findViewById(R.id.Seed_Info);
		butDone = (ImageButton) view.findViewById(R.id.edit_field_butDone);
		etComment = (EditText) view.findViewById(R.id.edit_field_etComment);
		butDelete = (ImageButton) view.findViewById(R.id.edit_field_butDelete);

		chkPlanned.setOnCheckedChangeListener(this);
		chkStarted.setOnCheckedChangeListener(this);
		chkDone.setOnCheckedChangeListener(this);

		butEditField.setOnClickListener(this);
		butCalendar.setOnClickListener(this);

		butDone.setOnClickListener(this);
		etComment.addTextChangedListener(new MyTextWatcher(etComment));
		butDelete.setOnClickListener(this);

		loadWorkerList();
		spinWorker.setOnItemSelectedListener(this);
		spinWorkerAdapter = new ArrayAdapter<Worker>(this.getActivity(),
				//android.R.layout.simple_list_item_1, workerList);
				R.layout.seed_textview,R.id.SeedListLine, workerList);
		spinWorker.setAdapter(spinWorkerAdapter);
		butNewWorker.setOnClickListener(this);
		loadSeedList();
		spinSeed.setOnItemSelectedListener(this);
		spinSeedAdapter = new ArrayAdapter<Seed>(this.getActivity(),
				R.layout.seed_textview,R.id.SeedListLine, seedList);
		spinSeed.setAdapter(spinSeedAdapter);
		butNewSeed.setOnClickListener(this);
		
		etSeednote.addTextChangedListener(new MyTextWatcher(etSeednote));
		
		SeedInfo.addTextChangedListener(new MyTextWatcher(SeedInfo));
		
		CameraButton = (ImageButton) view.findViewById(R.id.CameraButton);
		CameraButton.setOnClickListener(this);
		CameraResult = (ImageButton) view.findViewById(R.id.CameraResult);
		CameraResult.setOnClickListener(this);
		
		AddSubNote = (ImageButton) view
				.findViewById(R.id.add_subnote);
		AddSubNote.setOnClickListener(this);
		list_notes= (LinearLayout) view.findViewById(R.id.list_notes);
		
		vi = (LayoutInflater) this.getActivity().getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return view;
		
		
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getData();
		// Return from save
		if (savedInstanceState != null) {
			// Populate info
			currentJob.setStatus(savedInstanceState.getInt("EditJobStatus"));
			currentJob.setWorkerName(savedInstanceState
					.getString("EditJobWorkerName"));
			currentJob.setSeedName(savedInstanceState
					.getString("EditJobSeedName"));
			currentJob.setComments(savedInstanceState
					.getString("EditJobComments"));
			currentJob.setSeednotes(savedInstanceState
					.getString("EditJobSeednotes"));
			currentJob.setDateOfOperation(savedInstanceState
					.getString("EditJobDateOfOperation"));
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (currentJob != null)
			outState.putInt("EditJobStatus", currentJob.getStatus());
		if (currentJob != null)
			outState.putString("EditJobWorkerName", currentJob.getWorkerName());
			outState.putString("EditJobSeedName", currentJob.getSeedName());
			outState.putString("EditJobComments", currentJob.getComments());
			outState.putString("EditJobSeednotes", currentJob.getSeednotes());
		if (currentJob != null)
			outState.putString("EditJobDateOfOperation",
					currentJob.getDateOfOperation());
		super.onSaveInstanceState(outState);
	}

	private void getData() {
		Log.d("FragmentEditJobPopup", "getData()");
		currentField = listener.EditJobGetCurrentField();
		currentJob = listener.EditJobGetCurrentJob();
		// Grab data from field and populate views
		if (currentField != null) {
			tvAcres.setVisibility(View.VISIBLE);
			butEditField.setVisibility(View.VISIBLE);
			if (currentField.getName().length() != 0) tvName.setText(currentField.getName());
			tvAcres.setText(Integer.toString(currentField.getAcres()));
			list_notes.removeAllViews();
			notes = Note.FindNotesByFieldName(dbHelper.getReadableDatabase(), currentField.getName());
			Log.d("FragmentEditJobPopup", "Number of notes loaded:" + Integer.toString(notes.size()));
			if(currentJob != null) {
				currentJob.setNotes(notes);
			}
			for(int i=0; i<notes.size(); i++){
				//Add note to list
				list_notes.addView(inflateNote(notes.get(i)));
			}
		} else {
			Log.d("FragmentEditJobPopup - getData", "ERROR - current field is null from activity");
			notes = null;
			if(currentJob != null){
				//Deleted field but it is still a job, disable field edit and acres
				tvName.setText(currentJob.getFieldName());
				tvAcres.setVisibility(View.GONE);
				butEditField.setVisibility(View.GONE);
			}
		}

		loadWorkerList();
		loadSeedList();

		chkPlanned.setOnCheckedChangeListener(null);
		chkStarted.setOnCheckedChangeListener(null);
		chkDone.setOnCheckedChangeListener(null);
		// Grab data from job and populate views
		if (currentJob != null) {
			chkPlanned.setChecked(false);
			chkDone.setChecked(false);
			chkStarted.setChecked(false);
			if (currentJob.getStatus() == Job.STATUS_PLANNED) {
				chkPlanned.setChecked(true);
			} else if (currentJob.getStatus() == Job.STATUS_STARTED) {
				chkStarted.setChecked(true);
			} else if (currentJob.getStatus() == Job.STATUS_DONE) {
				chkDone.setChecked(true);
			}

			// Spinner
			selectWorkerInSpinner(currentJob.getWorkerName());
			selectSeedInSpinner(currentJob.getSeedName());
			etComment.setText(currentJob.getComments());
			etSeednote.setText(currentJob.getSeednotes());
			Date d = DatabaseHelper.stringToDateLocal(currentJob
					.getDateOfOperation());
			if (dateIsToday(d)) {
				tvCalendar.setText("Today");
			} else {
				SimpleDateFormat displayFormat = new SimpleDateFormat(
						"MMM, dd", Locale.US);
				tvCalendar.setText(displayFormat.format(d));
			}
		} else if(currentField != null) {
			// New Job
			chkPlanned.setChecked(false);
			chkDone.setChecked(false);
			chkStarted.setChecked(false);
			Log.d("FragmentEditJobPopup - getData",
					"Making new job, currentJob is null");
			currentJob = new Job(currentField.getName());
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this.getActivity()
							.getApplicationContext());
			currentJob.setWorkerName(prefs.getString("WorkerName", ""));
			// set dateChanged and dateOfOperation to Now
			currentJob.setSeedName(prefs.getString("SeedName", ""));
			currentJob.setDateOfOperation(DatabaseHelper
					.dateToStringLocal(new Date()));
			currentJob.setDateChanged(DatabaseHelper
					.dateToStringUTC(new Date()));
		}
		chkPlanned.setOnCheckedChangeListener(this);
		chkStarted.setOnCheckedChangeListener(this);
		chkDone.setOnCheckedChangeListener(this);
	}

	public void refreshData() {
		Log.d("FragmentEditJobPopup", "RefreshData");
		getData();
	}

	public void updateDate(int year, int month, int day) {
		// Date from datepicker
		// Month is 0 based, just add 1
		String dateWyear = Integer.toString(year) + "-"
				+ Integer.toString(month + 1) + "-" + Integer.toString(day);
		SimpleDateFormat dateFormaterLocal = new SimpleDateFormat("yyyy-M-d",
				Locale.US);
		Date d;
		try {
			d = dateFormaterLocal.parse(dateWyear);
		} catch (ParseException e) {
			d = new Date(0);
		}
		currentJob.setDateOfOperation(DatabaseHelper.dateToStringLocal(d));
		if (dateIsToday(d)) {
			tvCalendar.setText("Today");
		} else {
			SimpleDateFormat displayFormat = new SimpleDateFormat("MMM, dd",
					Locale.US);
			tvCalendar.setText(displayFormat.format(d));
		}
		flushChangesAndSave(false, false); // Save changes
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (activity instanceof EditJobListener) {
			listener = (EditJobListener) activity;
		} else {
			throw new ClassCastException(activity.toString()
					+ " must implement FragmentEditJob.EditJobListener");
		}
		Log.d("FragmentEditJobPopup", "Attached");
	}
	private View inflateNote(Note note){
		View view = vi.inflate(R.layout.note_row, null);
		NoteView noteView = new NoteView();
		noteView.notePair = (RelativeLayout) view.findViewById(R.id.note_pair);
		noteView.Topic = (EditText) view.findViewById(R.id.edit_note_name1);
		noteView.Content = (EditText) view.findViewById(R.id.edit_note_context1);
		noteView.Comment = (TextView) view.findViewById(R.id.colon);
		noteView.me = view;
		noteView.note = note;
		
		noteView.Topic.setText(note.getTopic());
		noteView.Content.setText(note.getComment());
		
		noteView.Topic.setTag(noteView);
		noteView.Content.setTag(noteView);
		
		noteView.Topic.addTextChangedListener(new MyTextWatcher(noteView.Topic));
		noteView.Content.addTextChangedListener(new MyTextWatcher(noteView.Content));
		view.setTag(noteView);
		return view;
	}
	static class NoteView
    {
		TextView Comment;
		EditText Topic;
		EditText Content;
		Note note;
		RelativeLayout notePair;
		View me;
    }

	@Override
	public void onClick(View v) {
		NoteView noteView = (NoteView) v.getTag();
		if (v.getId() == R.id.edit_field_butDone) {
			// Pass all info back to activity
			// TODO check if has changes			
			flushChangesAndSave(true);
		} else if (v.getId() == R.id.edit_field_butDelete) {
			new AlertDialog.Builder(this.getActivity())
					.setTitle("Delete Job")
					.setMessage("Are you sure you want to delete this job?")
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									listener.EditJobDelete();
								}
							}).setNegativeButton("No", null).show();
		} else if (v.getId() == R.id.edit_field_butCalendar) {
			DatePickerFragment newFragment = new DatePickerFragment();
			newFragment.setDate(DatabaseHelper.stringToDateLocal(currentJob
					.getDateOfOperation()));
			newFragment.show(this.getActivity().getSupportFragmentManager(),
					"datePicker");
		} else if (v.getId() == R.id.edit_field_butNewOperator) {
			// Create new worker
			createWorker();
		} else if (v.getId() == R.id.edit_field_butNewSeed) {
			// Create new worker
			createSeed();
		} else if (v.getId() == R.id.edit_field_butEditField) {
			listener.EditJobEditField();
		} else if (v.getId() == R.id.add_subnote) {
			//Add a new note
			Note newNote = new Note(currentField.getName());
			list_notes.addView(inflateNote(newNote), notes.size());
			notes.add(newNote);
			Log.d("Subnote", "Button responds");
		} else if (v.getId() == R.id.CameraButton) {
            takePhoto();
            
		} else if (v.getId() == R.id.CameraResult) {
			loadPhoto(CameraResult, 100, 100);			
        } 
	}

	private void takePhoto(){
		Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        //File file = new File(Environment.getExternalStorageDirectory()+ File.separator + "image.jpg");
        File imagesFolder = new File(File.separator +"sdcard"+File.separator +"OpenAtk Planting App Seed Photos");
        imagesFolder.mkdirs(); // <----
        if(imageSeedName == null) imageSeedName = "Null";
        Log.d("Camera Image naming","Image name="+ imageSeedName);
        image = new File(imagesFolder,imageSeedName + ".jpg");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(image));
        this.getActivity().startActivityForResult(intent, 100); 
	}


	private void loadPhoto(ImageView imageView, int width, int height) {		 	
	        ImageView tempImageView = imageView;


	        AlertDialog.Builder imageDialog = new AlertDialog.Builder(this.getActivity())
	        .setPositiveButton("Replace Current Seed Photo",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							takePhoto();
						}
					})
			.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
	        imageD = imageDialog.create();
	        
	        LayoutInflater inflater = (LayoutInflater) LayoutInflater.from(this.getActivity());

	        layout = inflater.inflate(R.layout.custom_fullimage_dialog,(ViewGroup) getActivity().findViewById(R.id.layout_root));
	        ImageView image = (ImageView) layout.findViewById(R.id.fullimage);
	        image.setImageDrawable(tempImageView.getDrawable());
	        imageDialog.setView(layout);	        
	        imageDialog.create();
	        imageDialog.show(); 
	        imageD.cancel();
	    }
	public void changeCameraIcon(){
		Log.d("camera","icon changed" + image.getAbsolutePath());
		Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
		int nh = (int) ( bitmap.getHeight() * (1024.0 / bitmap.getWidth()) );		
		Bitmap scaled = Bitmap.createScaledBitmap(bitmap, 1024, nh, true);
        this.CameraResult.setImageBitmap(scaled);
		bitmap = null;
		scaled = null;
        Log.d("Icon changed","Confirmed");
        if(seedObjectImage != null) {
			CameraResult.setVisibility(View.VISIBLE);
			CameraButton.setVisibility(View.GONE);// Go back to
        	//seed.setImage(image.getAbsolutePath());
        	SQLiteDatabase database = dbHelper.getWritableDatabase();
     		ContentValues values = new ContentValues();
     		values.put(TableSeed.COL_IMAGE,image.getAbsolutePath());
     		String where = TableSeed.COL_ID + " = " + Integer.toString(seedObjectImage.getId());
     		database.update(TableSeed.TABLE_NAME, values, where, null);
     		dbHelper.close();
    		Log.d("Icon changed","Save where:"  + where);
    		Log.d("Icon changed","Save path:"  + image.getAbsolutePath());
    		Log.d("Icon changed","Saved new Image to Seed Database"  + ":" + Integer.toString(seedObjectImage.getId()));
        } else {
    		Log.d("Icon changed","seedObjectImage is null ERROR");
        }        
	}

	public void flushChangesAndSave(Boolean changeState, Boolean unselect) {
		flush();
		listener.EditJobSave(currentJob, changeState, unselect);
	}

	public void flushChangesAndSave(Boolean changeState) {
		flush();
		listener.EditJobSave(currentJob, changeState, true);
	}

	private void flush() {
		currentJob.setDateChanged(DatabaseHelper.dateToStringUTC(new Date()));
		currentJob.setHasChanged(1);
		currentJob.setNotes(notes);
	}

	public int getHeight() {
		// Method so close transition can work
		return getView().getHeight();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (currentJob != null) {
			if (isChecked) {
				Boolean changed = false;
				if (buttonView.getId() == R.id.edit_field_chkPlanned) {
					if(currentJob.getStatus() != Job.STATUS_PLANNED){
						changed = true;
						currentJob.setStatus(Job.STATUS_PLANNED);
					}
				} else if (buttonView.getId() == R.id.edit_field_chkStarted) {
					if(currentJob.getStatus() != Job.STATUS_STARTED){
						changed = true;
						currentJob.setStatus(Job.STATUS_STARTED);
					}
				} else if (buttonView.getId() == R.id.edit_field_chkDone) {
					if(currentJob.getStatus() != Job.STATUS_DONE){
						changed = true;
						currentJob.setStatus(Job.STATUS_DONE);
					}
				}
				if(changed) {
					if (currentJob.getStatus() == Job.STATUS_PLANNED) {
						chkDone.setChecked(false);
						chkStarted.setChecked(false);
						flushChangesAndSave(false, false);
					} else if (currentJob.getStatus() == Job.STATUS_STARTED) {
						chkPlanned.setChecked(false);
						chkDone.setChecked(false);
						flushChangesAndSave(false, false);
					} else if (currentJob.getStatus() == Job.STATUS_DONE) {
						chkPlanned.setChecked(false);
						chkStarted.setChecked(false);
						flushChangesAndSave(false, false);
					}
				}
			} else {
				if (buttonView.getId() == R.id.edit_field_chkPlanned && currentJob.getStatus() == Job.STATUS_PLANNED) {
					chkPlanned.setChecked(true);
				} else if (buttonView.getId() == R.id.edit_field_chkStarted && currentJob.getStatus() == Job.STATUS_STARTED) {
					chkStarted.setChecked(true);
				} else if (buttonView.getId() == R.id.edit_field_chkDone && currentJob.getStatus() == Job.STATUS_DONE) {
					chkDone.setChecked(true);
				}
			}
		}
	}

	private class MyTextWatcher implements TextWatcher {
		private View view;

		private MyTextWatcher(View view) {
			this.view = view;
		}

		public void beforeTextChanged(CharSequence charSequence, int i, int i1,
				int i2) {
		}

		public void onTextChanged(CharSequence charSequence, int i, int i1,
				int i2) {
		}

		public void afterTextChanged(Editable editable) {
			String text = editable.toString();
			if (view.getId() == R.id.edit_field_etComment) {
				currentJob.setComments(text);
			}
			if (view.getId() == R.id.Seed_Job_Note) {
				currentJob.setSeednotes(text);
			}
			if (view.getId() == R.id.edit_note_name1) {
				NoteView noteview = (NoteView) view.getTag();
				noteview.note.setTopic(text);
			}
			if (view.getId() == R.id.edit_note_context1) {
				NoteView noteview = (NoteView) view.getTag();
				noteview.note.setComment(text);
			}
			if (view.getId() == R.id.Seed_Info){
				Seed seed = (Seed) view.getTag();
				if(seed != null){
					seed.setSeedinfo(text);
					Log.d("textwatcher", "saving to seed database" + Integer.toString(seed.getId()));
					SQLiteDatabase database = dbHelper.getWritableDatabase();
					ContentValues values = new ContentValues();
					values.put(TableSeed.COL_SEEDINFO,seed.getSeedinfo());
					String where = TableSeed.COL_ID + " = " + Integer.toString(seed.getId());
					database.update(TableSeed.TABLE_NAME, values, where, null);
					dbHelper.close();
				}
			}
		}
	}

	public static class DatePickerFragment extends DialogFragment implements
			DatePickerDialog.OnDateSetListener {

		EditJobListener listener;
		Date date;

		@Override
		public void onAttach(Activity activity) {
			// TODO Auto-generated method stub
			super.onAttach(activity);
			if (activity instanceof EditJobListener) {
				listener = (EditJobListener) activity;
			} else {
				throw new ClassCastException(activity.toString()
						+ " must implement FragmentEditJob.EditJobListener");
			}
			Log.d("FragmentEditJobPopup", "Attached");
		}

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// Use the current date as the default date in the picker
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);

			// Create a new instance of DatePickerDialog and return it
			return new DatePickerDialog(getActivity(), this, year, month, day);
		}

		public void setDate(Date date) {
			this.date = date;
		}

		public void onDateSet(DatePicker view, int year, int month, int day) {
			// Do something with the date chosen by the user
			listener.EditJobDateSave(year, month, day);
		}
	}

	private Boolean dateIsToday(Date compare) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd",
				Locale.US);
		String str1 = dateFormat.format(compare);
		String str2 = dateFormat.format(new Date());
		Log.d("FragmentEditJob", "Date:" + str1 + "=" + str2);
		if (str1.contentEquals(str2)) {
			return true;
		}
		return false;
	}

	// Worker Spinner
	private void createWorker() {
		// get prompts.xml view
		LayoutInflater li = LayoutInflater.from(this.getActivity());
		View promptsView = li.inflate(R.layout.new_worker_dialog, null);

		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this.getActivity());
		alertDialogBuilder.setView(promptsView);

		final EditText userInput = (EditText) promptsView
				.findViewById(R.id.new_worker_dialog_name);

		// set dialog message
		alertDialogBuilder
				.setCancelable(false)
				.setPositiveButton("Add",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// Create the operation
								String name = userInput.getText().toString();
								if (name.isEmpty() == false) {
									// Create new worker
									SQLiteDatabase database = dbHelper
											.getWritableDatabase();
									ContentValues values = new ContentValues();
									values.put(TableOperations.COL_HAS_CHANGED,
											1);
									values.put(TableOperations.COL_NAME, name);
									database.insert(TableWorkers.TABLE_NAME,
											null, values);

									dbHelper.close();
									loadWorkerList();
									selectWorkerInSpinner(name);

									// Save this choice in preferences for next
									// open
									SharedPreferences prefs = PreferenceManager
											.getDefaultSharedPreferences(getActivity()
													.getApplicationContext());
									SharedPreferences.Editor editor = prefs
											.edit();
									editor.putString("defaultWorker", name);
									editor.commit();
								}
							}
						})
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.cancel();
							}
						});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	private void loadWorkerList() {
		if (spinWorkerAdapter != null)
			spinWorkerAdapter.clear();
		SQLiteDatabase database = dbHelper.getReadableDatabase();
		Cursor cursor = database.query(TableWorkers.TABLE_NAME,
				TableWorkers.COLUMNS, null, null, null, null, null);
		workerList = new ArrayList<Worker>();
		while (cursor.moveToNext()) {
			Worker worker = Worker.cursorToWorker(cursor);
			if (worker != null)
				workerList.add(worker);
			if (spinWorkerAdapter != null) {
				if (worker != null)
					spinWorkerAdapter.add(worker);
			}
		}
		cursor.close();
		dbHelper.close();

		// Add create
		if (workerList.isEmpty() == false) {
			spinWorker.setVisibility(View.VISIBLE);
			butNewWorker.setVisibility(View.GONE);

			Worker worker = new Worker();
			worker.setId(null);
			worker.setName("New Operator");
			workerList.add(worker);
			if (spinWorkerAdapter != null)
				spinWorkerAdapter.add(worker);

			if (spinWorkerAdapter != null)
				spinWorkerAdapter.notifyDataSetChanged();
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(this.getActivity()
							.getApplicationContext());
			String toSelect = prefs.getString("defaultWorker", null);
			selectWorkerInSpinner(toSelect);
		} else {
			// Show button and hide spinner
			spinWorker.setVisibility(View.GONE);
			butNewWorker.setVisibility(View.VISIBLE);
		}
	}

	private void selectWorkerInSpinner(String workerName) {
		if (spinWorkerAdapter != null && workerName != null) {
			Boolean found = false;
			Boolean selectOperatorFound = false;
			for (int i = 0; i < spinWorkerAdapter.getCount(); i++) {
				if (spinWorkerAdapter.getItem(i).getName()
						.contentEquals(workerName)) {
					spinWorker.setSelection(i);
					found = true;
					break;
				} else if(spinWorkerAdapter.getItem(i).getName()
						.contentEquals("Select Operator")){
					selectOperatorFound = true;
				}
			}
			if (found == false) {
				// Add this worker and select
				Worker newWorker = null;
				if(workerName.isEmpty() && selectOperatorFound == false){
					workerName = "Select Operator";
					newWorker = new Worker();
					newWorker.setName(workerName);
					newWorker.setId(-1);
				} else if(workerName.isEmpty() == false) {
					newWorker = new Worker();
					newWorker.setName(workerName);
					newWorker.setId(-2);
				} else if(workerName.isEmpty()) {	
					selectWorkerInSpinner("Select Operator");
				}
				if (newWorker != null) {
					workerList.add(newWorker);
					if (spinWorkerAdapter != null) {
						spinWorkerAdapter.add(newWorker);
					}
					selectWorkerInSpinner(workerName);
				}				
			}
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id) {
		if (parent== spinWorker){
		Worker worker = (Worker) parent.getItemAtPosition(pos);
		Log.d("Selected:", worker.getName());
		if (worker.getId() == null) {
			// Create new operation
			selectWorkerInSpinner(currentJob.getWorkerName()); // Go back to
																// original for
																// now, in case
																// cancel
			createWorker();
		} else {
			String newName = worker.getName();
			if(worker.getId() == -1) newName = ""; //"Select Operator" selected
			currentJob.setWorkerName(newName);
			if(worker.getId() > 0){
				// Save this choice in preferences for next open
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(this.getActivity()
								.getApplicationContext());
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString("defaultWorker", worker.getName());
				editor.commit();
			}
		}
		}
		if (parent == spinSeed){
		Seed seed = (Seed) parent.getItemAtPosition(pos);
		Log.d("Selected:", seed.getName());
		if (seed.getId() == null) {
			// Create new operation
			selectSeedInSpinner(currentJob.getSeedName());
			//CameraResult.setVisibility(View.GONE);// Go back to
			//CameraButton.setVisibility(View.GONE);													// original for
																// now, in case
																// cancel
			createSeed();
		} else {
			String newSeed = seed.getName();
			if(seed.getId() == -1) newSeed = ""; //"Select Seed" selected
			currentJob.setSeedName(newSeed);
			CameraResult.setVisibility(View.GONE);
			CameraButton.setVisibility(View.VISIBLE);
			if(seed.getId() > 0){
				// Save this choice in preferences for next open
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(this.getActivity()
								.getApplicationContext());
				SharedPreferences.Editor editor = prefs.edit();
				editor.putString("defaultSeed", seed.getName());
				editor.commit();
				seed = Seed.FindSeedNoteBySeedID(dbHelper.getReadableDatabase(), seed.getId());
				Log.d("OnItemSelected", "Load seed from db:" + Integer.toString(seed.getId()) + " : " + seed.getSeedinfo());
				SeedInfo.setTag(seed);
				SeedInfo.setText(seed.getSeedinfo());	
				imageSeedName = seed.getName();
				String imagePath = seed.getImage();
				seedObjectImage = Seed.FindSeedNoteBySeedID(dbHelper.getReadableDatabase(), seed.getId());

				if(imagePath != null) {
					image = new File(imagePath);
                	changeCameraIcon();
                	CameraResult.setVisibility(View.VISIBLE);
                	CameraButton.setVisibility(View.GONE);
                } else {
                	Log.d("Spinner", "imagePath is null");
                	CameraResult.setVisibility(View.GONE);
                }
				
			}
		}
		}
	}

	
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}
	
	// Worker Spinner
		private void createSeed() {
			// get prompts.xml view
			LayoutInflater li = LayoutInflater.from(this.getActivity());
			View promptsView = li.inflate(R.layout.new_seed_dialog, null);

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this.getActivity());
			alertDialogBuilder.setView(promptsView);

			final EditText userInput = (EditText) promptsView
					.findViewById(R.id.new_seed_dialog_name);

			// set dialog message
			alertDialogBuilder
					.setCancelable(false)
					.setPositiveButton("Add",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									// Create the operation
									String name = userInput.getText().toString();
									if (name.isEmpty() == false) {
										// Create new worker
										SQLiteDatabase database = dbHelper
												.getWritableDatabase();
										ContentValues values = new ContentValues();
										values.put(TableOperations.COL_HAS_CHANGED,
												1);
										values.put(TableOperations.COL_NAME, name);
										database.insert(TableSeed.TABLE_NAME,
												null, values);

										dbHelper.close();
										loadSeedList();
										selectSeedInSpinner(name);

										// Save this choice in preferences for next
										// open
										SharedPreferences prefs = PreferenceManager
												.getDefaultSharedPreferences(getActivity()
														.getApplicationContext());
										SharedPreferences.Editor editor = prefs
												.edit();
										editor.putString("defaultSeed", name);
										editor.commit();
									}
								}
							})
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int id) {
									dialog.cancel();
								}
							});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
		}

		private void loadSeedList() {
			if (spinSeedAdapter != null)
				spinSeedAdapter.clear();
			SQLiteDatabase database = dbHelper.getReadableDatabase();
			Cursor cursor = database.query(TableSeed.TABLE_NAME,TableSeed.COLUMNS, null, null, null, null, null);
			seedList = new ArrayList<Seed>();
			while (cursor.moveToNext()) {
				Seed seed = Seed.cursorToSeed(cursor);
				if (seed != null)
					seedList.add(seed);
				if (spinSeedAdapter != null) {
					if (seed != null)
						spinSeedAdapter.add(seed);
				}
			}
			cursor.close();
			dbHelper.close();

			// Add create
			if (seedList.isEmpty() == false) {
				spinSeed.setVisibility(View.VISIBLE);
				butNewSeed.setVisibility(View.GONE);

				Seed seed = new Seed();
				seed.setId(null);
				seed.setName("New Seed");
				seedList.add(seed);
				if (spinSeedAdapter != null)
					spinSeedAdapter.add(seed);

				if (spinSeedAdapter != null)
					spinSeedAdapter.notifyDataSetChanged();
				SharedPreferences prefs = PreferenceManager
						.getDefaultSharedPreferences(this.getActivity()
								.getApplicationContext());
				String toSelect = prefs.getString("defaultSeed", null);
				selectSeedInSpinner(toSelect);
			} else {
				// Show button and hide spinner
				spinSeed.setVisibility(View.GONE);
				butNewSeed.setVisibility(View.VISIBLE);
			}
		}

		private void selectSeedInSpinner(String seedName) {
			if (spinSeedAdapter != null && seedName != null) {
				Boolean found = false;
				Boolean selectSeedFound = false;
				for (int i = 0; i < spinSeedAdapter.getCount(); i++) {
					if (spinSeedAdapter.getItem(i).getName()
							.contentEquals(seedName)) {
						spinSeed.setSelection(i);
						found = true;
						break;
					} else if(spinSeedAdapter.getItem(i).getName()
							.contentEquals("Select Seed")){
						selectSeedFound = true;
					}
				}
				if (found == false) {
					// Add this worker and select
					Seed newSeed = null;
					if(seedName.isEmpty() && selectSeedFound == false){
						seedName = "Select Seed";
						newSeed = new Seed();
						newSeed.setName(seedName);
						newSeed.setId(-1);
					} else if(seedName.isEmpty() == false) {
						newSeed = new Seed();
						newSeed.setName(seedName);
						newSeed.setId(-2);
					} else if(seedName.isEmpty()) {	
						selectSeedInSpinner("Select Seed");
					}
					if (newSeed != null) {
						seedList.add(newSeed);
						if (spinSeedAdapter != null) {
							spinSeedAdapter.add(newSeed);
						}
						selectSeedInSpinner(seedName);
					}				
				}
			}
		}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		float eventY = event.getRawY();
		
		switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
            	listener.SliderDragDown((int)eventY);
               break; 
            }
            case MotionEvent.ACTION_UP:
            {     
            	listener.SliderDragUp((int)(eventY));
                 break;
            }
            case MotionEvent.ACTION_MOVE:
            {
            	listener.SliderDragDragging((int)(eventY));
                break;
            }
        }
        return true;
	}
}
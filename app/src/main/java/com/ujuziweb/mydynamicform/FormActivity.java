package com.ujuziweb.mydynamicform;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ujuziweb.mydynamicform.R.string;
import com.ujuziweb.mydynamicform.accordion.FormAccordion;
import com.ujuziweb.mydynamicform.camera.FormCamera;
import com.ujuziweb.mydynamicform.spinner.SelectionHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import makemachine.android.examples.forms.R;
//import makemachine.android.examples.forms.R.string;

/**
 * FormActivity allows you to create dynamic form layouts based upon a json
 * schema file. This class should be sub-classed.
 * 
 * All buttons on this screen are created without any action.
 * 
 * The library provides only the components, the application rules will stay on
 * the class that extends this class.
 * 
 * @author Jeremy Brown
 * @author Paulo Luan
 * 
 * */
public class FormActivity extends Activity implements FormInterface {
        public static final String            SCHEMA_KEY_ID                    = "id";
        public static final String            SCHEMA_KEY_NAME                  = "name";
        public static final String            SCHEMA_KEY_TYPE                  = "type";
        
        public static final String            SCHEMA_KEY_CHECKBOX              = "Checkbox";
        public static final String            SCHEMA_KEY_SPINNER               = "Spinner";
        public static final String            SCHEMA_KEY_INTEGER_TEXTVIEW      = "IntegerTextView";
        public static final String            SCHEMA_KEY_STRING_TEXTVIEW       = "StringTextView";
        public static final String            SCHEMA_KEY_DATE_TEXTVIEW         = "DateTextView";
        public static final String            SCHEMA_KEY_AUTOCOMPLETE_TEXTVIEW = "AutoCompleteTextView";
        public static final String            SCHEMA_KEY_LABEL                 = "Label";
        public static final String            SCHEMA_KEY_ACCORDION             = "Accordion";
        public static final String            SCHEMA_KEY_CAMERA                = "Camera";
        public static final String            SCHEMA_KEY_SUBFORM               = "Subform";
        public static final String            SCHEMA_KEY_SUBFORM_SOURCE        = "source";

        public static final String            SCHEMA_KEY_PRIORITY              = "priority";
        public static final String            SCHEMA_KEY_TOGGLES               = "toggles";
        public static final String            SCHEMA_KEY_ACTIONS               = "hideOnClick";
        public static final String            SCHEMA_KEY_DEFAULT               = "default";
        public static final String            SCHEMA_KEY_MODIFIERS             = "modifiers";
        public static final String            SCHEMA_KEY_OPTIONS               = "options";
        public static final String            SCHEMA_KEY_ACCORDION_CHILDREN    = "children";
        public static final String            SCHEMA_KEY_META                  = "meta";
        public static final String            SCHEMA_KEY_HINT                  = "hint";
        public static final String            SCHEMA_KEY_REQUIRED              = "required";

        public static final LayoutParams      defaultLayoutParams              = new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
        
        // -- data
        public static Map<String, FormWidget> _map;
        protected ArrayList<FormWidget>       _widgets;
        
        // -- widgets
        protected LinearLayout                _formLayout;
        protected ScrollView                  _scrollView;
        protected TextView                      _textView;
        
        // -- Buttons
        public Button                         buttonSubform;
        public Button                         buttonDelete;
        public Button                         buttonSave;
        public Button                         buttonCancel;
        public Button                         buttonClear;
        
        private List<String>                  photos;
        private String                        idHash;
        public String           subFormSource;
        public String   subFormID;
        // -----------------------------------------------
        //
        // Context menu options.
        //
        // -----------------------------------------------
        public static final int               OPTION_SAVE                      = 0;
        public static final int               OPTION_POPULATE                  = 1;
        public static final int               OPTION_CANCEL                    = 2;
        private static final String           LOG_TAG                          = "FORM_ACTIVITY";
        
        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
                menu.add(0, OPTION_SAVE, 0, "Sync");
                /*menu.add(0, OPTION_POPULATE, 0, "Populate");
                menu.add(0, OPTION_CANCEL, 0, "Cancel");*/
                return true;
        }
        
        @Override
        public boolean onMenuItemSelected(int id, MenuItem item) {
                
                switch (item.getItemId()) {
                        case OPTION_SAVE:
                                //save();
                                //syncTask();
                                break;
                        
                        case OPTION_POPULATE:
                                populate(FormActivity.parseFileToString(this, "bauru_data.json"));
                                break;
                        
                        case OPTION_CANCEL:
                                break;
                }
                
                return super.onMenuItemSelected(id, item);
        }
        
        // -----------------------------------------------
        //
        // parse data and build view
        //
        // -----------------------------------------------
        
        /**
         * Call the functions that initialize the widgets and layout.
         * 
         * @param data
         *                - the raw json data as a String
         */
        public void generateForm(String data) {

                /*setTitle(R.string.app_name);*/

                initializeContentView();
                
                initializeWidgets(data);
                initializeAccordions();
                //addWidgetsToLayout(_widgets);
                
                initToggles();
                initializeWidgetsVisibility();
                
                createMenuButtons();

                //createSubFormButton();

                //Toast.makeText(getApplicationContext(), "Sub Source: " + subFormID, Toast.LENGTH_LONG).show();
        }
        
        /**
         * @param data
         *                - the raw json data as a String
         */
        public void initializeWidgets(String data) {
                _widgets = new ArrayList<FormWidget>();
                _map = new HashMap<String, FormWidget>();
                
                List<FormWidget> widgets = parseJsonToWidgets(data);
                
                for (FormWidget formWidget : widgets) {
                        _widgets.add(formWidget);
                        _map.put(formWidget.getId(), formWidget);
                }
        }
        
        public void initializeContentView() {

               /* _textView = new TextView(this);
                _textView.setText(string.app_name);*/

                // -- create the layout
                _scrollView = new ScrollView(this);
                _scrollView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
                _scrollView.setBackgroundColor(Color.WHITE);
                
                _formLayout = new LinearLayout(this);
                _formLayout.setOrientation(LinearLayout.VERTICAL);
                defaultLayoutParams.setMargins(5, 5, 5, 5);
                _formLayout.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));

               /* _scrollView.addView(_textView);*/
                _scrollView.addView(_formLayout);
                
                setContentView(_scrollView);
        }
        
        public void addWidgetsToLayout(List<FormWidget> widgets) {
                // -- sort widgets on priority
                Collections.sort(widgets, new PriorityComparison());
                
                for (int i = 0; i < widgets.size(); i++) {
                        _formLayout.addView((View) widgets.get(i).getView());
                }
        }
        
        /**
         * É necessário criar uma lista secundária pois a lista chamada
         * "_widgets" é utilizada como forma a referenciar todos os widgets
         * criados no formulário, e para que o accordion funcione é necessário
         * que todos os widgets filhos sejam removidos da lista para que este
         * widgets em específico sejam adicionados somente ao layout do
         * accordion. Em contrapartida não podemos remover este objeto do
         * "_widgets" pois ele é utilizado para ser referenciado por toda a
         * aplicação como forma de identificar todos os componentes.
         */
        public void initializeAccordions() {
                List<FormWidget> clonedWidgets = new ArrayList<FormWidget>(_widgets);
                
                for (FormWidget widget : _widgets) {
                        
                        if (widget instanceof FormAccordion) {
                                FormAccordion accordion = (FormAccordion) widget;
                                
                                List<String> children = accordion.getChildrenIds();
                                
                                //Iterate over all children of the accordion, and remove it from the cloned list
                                for (String childId : children) {
                                        FormWidget childWidget = _map.get(childId);
                                        
                                        if (childWidget != null) {
                                                accordion.addChildWidget(childWidget._layout);
                                                clonedWidgets.remove(childWidget);
                                        }
                                }
                        }
                }
                
                addWidgetsToLayout(clonedWidgets);
        }
        
        /**
         * 
         * Parses a supplied schema of raw json data and creates widgets
         * 
         * @param json
         * @return the conversion of the json to an array of widgets.
         * @throws JSONException
         */
        public List<FormWidget> parseJsonToWidgets(String json) {
                List<FormWidget> widgets = new ArrayList<FormWidget>();
                
                try {
                        JSONArray jsonArray = new JSONArray(json);
                        
                        for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonWidget = jsonArray.getJSONObject(i);
                                FormWidget formWidget = parseWidget(jsonWidget);
                                
                                if (formWidget != null) {
                                        widgets.add(formWidget);
                                }
                        }
                }
                catch (JSONException e) {
                        Log.e(LOG_TAG, e.getMessage());
                }
                
                return widgets;
        }
        
        /**
         * Creates object as its configuration.
         * 
         * @param jsonWidget
         * @return the object with his appropriated cast.
         * @throws JSONException
         */
        public FormWidget parseWidget(JSONObject jsonWidget) throws JSONException {
                FormWidget widget = null;
                
                String widgetLabelName = jsonWidget.getString(SCHEMA_KEY_NAME);
                
                if (!widgetLabelName.equals(SCHEMA_KEY_META)) {
                        widget = getWidget(widgetLabelName, jsonWidget);
                        
                        if (widget != null) {


                                String id = jsonWidget.getString(FormActivity.SCHEMA_KEY_ID);
                                widget.setId(id);
                                
                                int priority = jsonWidget.getInt(FormActivity.SCHEMA_KEY_PRIORITY);
                                widget.setPriority(priority);
                                
                                String defaultValue = getDefault(jsonWidget);
                                //widget.setValue(defaultValue);
                                
                                String type = jsonWidget.getString(FormActivity.SCHEMA_KEY_TYPE);
                                
                                if (type.equals(SCHEMA_KEY_CHECKBOX)) {
                                        boolean toggles = hasPropertie(jsonWidget, FormActivity.SCHEMA_KEY_TOGGLES);
                                        
                                        if (toggles) {
                                                ((FormCheckBox) widget).setToggles(parseProperties(jsonWidget, FormActivity.SCHEMA_KEY_TOGGLES));
                                                ((FormCheckBox) widget).setToggleHandler(new FormActivity.FormWidgetToggleHandler());
                                        }
                                }
                                else if (type.equals(SCHEMA_KEY_SPINNER)) {
                                        boolean widgetsToHide = hasPropertie(jsonWidget, FormActivity.SCHEMA_KEY_ACTIONS);
                                        
                                        if (widgetsToHide) {
                                                HashMap<String, ArrayList<String>> actions = parseProperties(jsonWidget, FormActivity.SCHEMA_KEY_ACTIONS);
                                                ((FormSpinner) widget).setWidgetsToHide(actions);
                                        }
                                }
                                else if (type.equals(SCHEMA_KEY_DATE_TEXTVIEW)) {

                                        if(defaultValue.equals("_today")){
                                                final Calendar c = Calendar.getInstance();
                                                int year = c.get(Calendar.YEAR);
                                                int month = c.get(Calendar.MONTH);
                                                int day = c.get(Calendar.DAY_OF_MONTH);
                                                String currentDate = (month + 1) + "-" + (day) + "-" + (year) + "";

                                                widget.setValue(currentDate);
                                        }

                                }
                                else if (type.equals(SCHEMA_KEY_SUBFORM)) {
                                        String source = jsonWidget.getString(FormActivity.SCHEMA_KEY_SUBFORM_SOURCE);
                                        String btn_id = jsonWidget.getString(FormActivity.SCHEMA_KEY_ID);

                                        subFormSource = source;
                                        subFormID = widget.getId();
                                        widget.setVisibility(0);
                                        widget.setValue("");
                                }

                                if (jsonWidget.has(FormActivity.SCHEMA_KEY_HINT)) widget.setHint(jsonWidget.getString(FormActivity.SCHEMA_KEY_HINT));
                        }
                        
                }
                
                return widget;
        }
        
        // -----------------------------------------------
        //
        // populate and save
        //
        // -----------------------------------------------
        
        /**
         * this method fills the form with existing data get the json string
         * stored in the record we are editing create a json object ( if this
         * fails then we know there is now existing record ) create a list of
         * property names from the json object loop through the map returned by
         * the Form class that maps widgets to property names if the map
         * contains the property name as a key that means there is a widget to
         * populate w/ a value
         */
        protected void populate(String jsonString) {
                JSONObject data = null;
                JSONArray properties = null;
                
                try {
                        data = new JSONObject(jsonString);
                        properties = data.names();
                }
                catch (JSONException e) {
                        Log.e(LOG_TAG, e.getMessage());
                }
                
                if (data != null && properties != null) {
                        processProperties(data, properties);
                }
        }
        
        public void processProperties(JSONObject data, JSONArray properties) {
                String property;
                FormWidget widget;
                
                for (int i = 0; i < properties.length(); i++) {
                        try {
                                property = properties.getString(i);
                                
                                if (_map.containsKey(property)) {
                                        widget = _map.get(property);
                                        
                                        if (widget instanceof FormCamera) {
                                                FormCamera formCamera = (FormCamera) widget;
                                                formCamera.setIdHash(idHash);
                                                
                                                String photosArray = data.getString(property);
                                                photos = Arrays.asList(photosArray.split("\\, "));
                                                
                                                formCamera.setPhotos(photos);
                                                formCamera.updatePhotos();
                                        }
                                        else {
                                                String value = data.getString(property);
                                                widget.setValue(value);
                                        }
                                }
                        }
                        catch (JSONException e) {
                                Log.e(LOG_TAG, e.getMessage());
                        }
                }
                
        }
        
        public ArrayList<String> jsonArrayToList(JSONArray jArray) {
                ArrayList<String> listdata = new ArrayList<String>();
                
                if (jArray != null) {
                        for (int i = 0; i < jArray.length(); i++) {
                                try {
                                        listdata.add(jArray.get(i).toString());
                                }
                                catch (JSONException e) {
                                        e.printStackTrace();
                                }
                        }
                }
                
                return listdata;
        }
        
        /**
         * this method preps the data and saves it if there is a problem w/
         * creating the json string, the method fails loop through each widget
         * and set a property on a json object to the value of the widget's
         * getValue() method
         */
        protected JSONObject save() {
                FormWidget widget;
                JSONObject data = new JSONObject();
                
                boolean success = true;
                
                try {
                        for (int i = 0; i < _widgets.size(); i++) {
                                widget = _widgets.get(i);
                                
                                boolean isFormLabelTitle = widget instanceof FormLabelTitle;
                                boolean isFormAccordion = widget instanceof FormAccordion;
                                boolean isFormInput = false;

                                if(widget instanceof FormEditText || widget instanceof FormDateText || widget instanceof FormNumericEditText){
                                        /*Toast.makeText(getApplicationContext(), "ID: " + propertyId, Toast.LENGTH_LONG).show();*/
                                        isFormInput = true;
                                }

                                if (!isFormLabelTitle && !isFormAccordion) {

                                        String propertyId = widget.getId();

                                        if (widget instanceof FormCamera) {
                                                FormCamera formCamera = (FormCamera) widget;
                                                String concatArray = Arrays.toString(formCamera.getPhotos().toArray());
                                                concatArray = concatArray.substring(1, concatArray.length() - 1);
                                                data.put(propertyId, concatArray);
                                                setPhotos(formCamera.getPhotos());
                                        }
                                        else if (widget instanceof FormEditText) {
                                                FormEditText formEditText = (FormEditText) widget;
                                                String value = formEditText.getValue();
                                                data.put(propertyId, value);
                                        }
                                        else if (widget instanceof FormDateText) {
                                                FormDateText formDateText = (FormDateText) widget;
                                                String value = formDateText.getValue();
                                                data.put(propertyId, value);
                                        }
                                        else {
                                                String value = widget.getValue();
                                                data.put(propertyId, value);
                                        }
                                }
                        }
                }
                catch (JSONException e) {
                        success = false;
                        Log.i("MakeMachine", "Save error - " + e.getMessage());
                        return null;
                }
                
                if (success) {
                        Log.i("MakeMachine", "Save success " + data.toString());
                        return data;
                }
                return null;
        }
        
        // -----------------------------------------------
        //
        // toggles
        //
        // -----------------------------------------------
        
        /**
         * creates the map a map of values for visibility and references to the
         * widgets the value affects
         */
        protected HashMap<String, ArrayList<String>> parseProperties(
                                                                     JSONObject property,
                                                                     String objectKey) {
                try {
                        ArrayList<String> toggled;
                        HashMap<String, ArrayList<String>> toggleMap = new HashMap<String, ArrayList<String>>();
                        
                        //JSONObject toggleList = property.getJSONObject(FormActivity.SCHEMA_KEY_TOGGLES);
                        JSONObject toggleList = property.getJSONObject(objectKey);
                        JSONArray toggleNames = toggleList.names();
                        
                        for (int j = 0; j < toggleNames.length(); j++) {
                                String toggleName = toggleNames.getString(j);
                                JSONArray toggleValues = toggleList.getJSONArray(toggleName);
                                toggled = new ArrayList<String>();
                                toggleMap.put(toggleName, toggled);
                                for (int k = 0; k < toggleValues.length(); k++) {
                                        toggled.add(toggleValues.getString(k));
                                }
                        }
                        
                        return toggleMap;
                        
                }
                catch (JSONException e) {
                        return null;
                }
        }
        
        /**
         * returns a boolean indicating that the supplied json object contains
         * the property.
         */
        protected boolean hasPropertie(JSONObject obj, String key) {
                try {
                        obj.getJSONObject(key);
                        return true;
                }
                catch (JSONException e) {
                        return false;
                }
        }
        
        /**
         * initializes the visibility of widgets that are togglable
         */
        protected void initToggles() {
                int i;
                FormWidget widget;
                
                for (i = 0; i < _widgets.size(); i++) {
                        widget = _widgets.get(i);
                        updateToggles(widget);
                }
        }
        
        /**
         * updates any widgets that need to be toggled on or off
         * 
         * @param widget
         */
        protected void updateToggles(FormWidget widget) {
                int i;
                String name;
                ArrayList<String> toggles;
                ArrayList<FormWidget> ignore = new ArrayList<FormWidget>();
                
                if (widget instanceof FormCheckBox) {
                        
                        toggles = ((FormCheckBox) widget).getToggledOn();
                        for (i = 0; i < toggles.size(); i++) {
                                name = toggles.get(i);
                                if (_map.get(name) != null) {
                                        FormWidget toggle = _map.get(name);
                                        ignore.add(toggle);
                                        toggle.setVisibility(View.VISIBLE);
                                }
                        }
                        
                        toggles = ((FormCheckBox) widget).getToggledOff();
                        for (i = 0; i < toggles.size(); i++) {
                                name = toggles.get(i);
                                if (_map.get(name) != null) {
                                        FormWidget toggle = _map.get(name);
                                        if (ignore.contains(toggle)) continue;
                                        toggle.setVisibility(View.GONE);
                                }
                        }
                }
        }
        
        /**
         * simple callbacks for widgets to use when their values have changed
         */
        class FormWidgetToggleHandler {
                public void toggle(FormWidget widget) {
                        updateToggles(widget);
                }
        }
        
        // -----------------------------------------------
        //
        // utils
        //
        // -----------------------------------------------
        
        protected String getDefault(JSONObject obj) {
                try {
                        return obj.getString(FormActivity.SCHEMA_KEY_DEFAULT);
                }
                catch (JSONException e) {
                        return null;
                }
        }
        
        /**
         * helper class for sorting widgets based on priority
         */
        class PriorityComparison implements Comparator<FormWidget> {
                public int compare(FormWidget item1, FormWidget item2) {
                        return item1.getPriority() > item2.getPriority() ? 1 : -1;
                }
        }
        
        /**
         * Factory method for actually instantiating widgets
         * 
         * @param labelName
         * @param jsonWidget
         * @return FormWidget
         */
        protected FormWidget getWidget(String labelName, JSONObject jsonWidget) {
                FormWidget formWidget = null;
                
                try {
                        String type = jsonWidget.getString(FormActivity.SCHEMA_KEY_TYPE);
                        JSONObject options = null;
                        
                        if (jsonWidget.has(FormActivity.SCHEMA_KEY_OPTIONS)) {
                                options = jsonWidget.getJSONObject(FormActivity.SCHEMA_KEY_OPTIONS);
                        }
                        
                        if (type.equals(FormActivity.SCHEMA_KEY_ACCORDION)) {
                                JSONArray children = jsonWidget.getJSONArray(FormActivity.SCHEMA_KEY_ACCORDION_CHILDREN);
                                
                                List<String> childrenIds = FormWidget.jsonArrayToJavaArray(children);
                                
                                formWidget = new FormAccordion(this, labelName, childrenIds);
                        }
                        
                        if (type.equals(FormActivity.SCHEMA_KEY_CAMERA)) {
                                formWidget = new FormCamera(this, labelName);
                        }
                        
                        if (type.equals(FormActivity.SCHEMA_KEY_STRING_TEXTVIEW)) {
                                formWidget = new FormEditText(this, labelName);
                        }
                        
                        if (type.equals(FormActivity.SCHEMA_KEY_AUTOCOMPLETE_TEXTVIEW)) {
                                formWidget = new FormAutoCompleteTextBox(this, labelName, options);
                        }
                        
                        if (type.equals(FormActivity.SCHEMA_KEY_LABEL)) {
                                formWidget = new FormLabelTitle(this, labelName, options);
                        }
                        
                        if (type.equals(FormActivity.SCHEMA_KEY_CHECKBOX)) {
                                formWidget = new FormCheckBox(this, labelName);
                        }
                        
                        if (type.equals(FormActivity.SCHEMA_KEY_INTEGER_TEXTVIEW)) {
                                formWidget = new FormNumericEditText(this, labelName);
                        }

                        if (type.equals(FormActivity.SCHEMA_KEY_DATE_TEXTVIEW)) {
                                formWidget = new FormDateText(this, labelName);
                        }
                        
                        if (type.equals(FormActivity.SCHEMA_KEY_SPINNER)) {
                                if (jsonWidget.has(FormActivity.SCHEMA_KEY_OPTIONS)) {
                                        formWidget = new FormSpinner(this, labelName, options);
                                }
                        }

                        if (type.equals(FormActivity.SCHEMA_KEY_SUBFORM)) {
                                String btn_id = jsonWidget.getString(FormActivity.SCHEMA_KEY_ID);
                                String btn_src = jsonWidget.getString(FormActivity.SCHEMA_KEY_SUBFORM_SOURCE);
                                formWidget = new FormSubFormButton(this, labelName, btn_id, btn_src);
                        }
                }
                catch (JSONException e) {
                        return null;
                }
                
                return formWidget;
        }
        
        public static String parseFileToString(Context context, String filename) {
                try {
                        InputStream stream = context.getAssets().open(filename);
                        int size = stream.available();
                        
                        byte[] bytes = new byte[size];
                        stream.read(bytes);
                        stream.close();
                        
                        return new String(bytes);
                        
                }
                catch (IOException e) {
                        Log.i("MakeMachine", "IOException: " + e.getMessage());
                }
                return null;
        }
        
        public void initializeWidgetsVisibility() {
                /**
                 * Make all widgets visible. to re-apply the visibility from the
                 * bottom to the top.
                 */
                for (FormWidget widget : _widgets) {
                        widget.setVisibility(View.VISIBLE);
                }
                
                /**
                 * Iterates over all widgets to re-apply business rules in
                 * cascade from the bottom to the top.
                 */
                int i;
                for (i = _widgets.size() - 1; i >= 0; i--) {
                        FormWidget widget = _widgets.get(i);
                        
                        if (widget instanceof FormSpinner) {
                                FormSpinner formSpinner = (FormSpinner) widget;
                                Spinner spinner = (Spinner) formSpinner._spinner;
                                
                                if (formSpinner.lastModifiedWidgets != null) {
                                        formSpinner.lastModifiedWidgets.clear();
                                }
                                
                                spinner.setSelection(0);
                                //Log.i(formSpinner._displayText, spinner.getSelectedItem().toString());
                                
                                formSpinner.applyModifications();
                                
                                //Log.i("Applying Handler", formSpinner._displayText);
                                SelectionHandler handler = new SelectionHandler(formSpinner);
                                spinner.setOnItemSelectedListener(handler);
                        }
                }
        }
        
        public void createMenuButtons() {
                TableLayout buttonsLayout = new TableLayout(this);
                buttonsLayout.setLayoutParams(new TableLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                
                TableRow row = new TableRow(this);

                if(subFormID != null){
                        row.addView(createSubFormButton());
                }/**/
                row.addView(createCancelButton());
                /*row.addView(createClearButton());*/
                row.addView(createSaveButton());
                
                buttonsLayout.addView(row);
                
                _formLayout.addView(buttonsLayout);
        }
        
        /**
         * ===============================================================
         * 
         * Button Save Functions
         * 
         * ===============================================================
         * */
        public Button createSaveButton() {
                buttonSave = new Button(this);
               // Drawable saveIcon = getResources().getDrawable(R.drawable.ic_action_save);
               // buttonSave.setCompoundDrawablesWithIntrinsicBounds(null, saveIcon, null, null);
                buttonSave.setText(getResources().getString(string.btn_save));
                
                // The last parameter "1" is the magic that refers to the Weight, this is necessary to align the three buttons into the container.   
                buttonSave.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
                
                setSaveButtonListener();
                
                return buttonSave;
        }
        
        public void setSaveButtonListener() {
                buttonSave.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                showConfirmSaveDialog();
                        }
                });
        }
        
        public void showConfirmSaveDialog() {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FormActivity.this);
                alertDialogBuilder.setTitle(string.lbl_caution);
                alertDialogBuilder.setMessage(string.lbl_msg_save).setCancelable(false).setPositiveButton(string.lbl_yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                saveTask(save().toString());
                                finish();
                        }
                }).setNegativeButton(string.lbl_no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                        }
                });
                
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
        }
        
        /**
         * ===============================================================
         * 
         * Delete button functions
         * 
         * ===============================================================
         * */
        public Button createDeleteButton() {
                buttonDelete = new Button(this);
                //Drawable saveIcon = getResources().getDrawable(R.drawable.ic_action_discard);
                //buttonDelete.setCompoundDrawablesWithIntrinsicBounds(null, saveIcon, null, null);
                buttonDelete.setText(getResources().getString(string.btn_save));
                
                // The last parameter "1" is the magic that refers to the Weight, this is necessary to align the three buttons into the container.   
                buttonDelete.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
                
                setDeleteButtonListener();
                
                return buttonDelete;
        }
        
        public void setDeleteButtonListener() {
                buttonDelete.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                showConfirmDeleteDialog();
                        }
                });
        }
        
        public void showConfirmDeleteDialog() {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FormActivity.this);
                alertDialogBuilder.setTitle(string.lbl_caution);
                alertDialogBuilder.setMessage("Confirm Delete?").setCancelable(false).setPositiveButton(string.lbl_yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                deleteTask();
                        }
                }).setNegativeButton(string.lbl_no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                setResult(RESULT_CANCELED, new Intent());
                                finish();
                        }
                });
                
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
        }
        
        public Button createClearButton() {
                buttonClear = new Button(this);
                //Drawable clearIcon = getResources().getDrawable(R.drawable.ic_action_discard);
                //buttonClear.setCompoundDrawablesWithIntrinsicBounds(null, clearIcon, null, null);
                buttonClear.setText(getResources().getString(string.btn_clear));
                
                buttonClear.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1));
                
                buttonClear.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                for (FormWidget widget : _widgets) {
                                        widget.clear();
                                }
                        }
                });
                
                return buttonClear;
        }
        
        public Button createCancelButton() {
                buttonCancel = new Button(this);
                //Drawable cancelIcon = getResources().getDrawable(R.drawable.ic_action_cancel);
                //buttonCancel.setCompoundDrawablesWithIntrinsicBounds(null, cancelIcon, null, null);
                buttonCancel.setText(getResources().getString(string.btn_cancel));
                
                buttonCancel.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f));
                
                buttonCancel.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                finish();
                        }
                });
                
                return buttonCancel;
        }


        public Button createSubFormButton() {
                buttonSubform = new Button(this);
                //Drawable cancelIcon = getResources().getDrawable(R.drawable.ic_action_cancel);
                //buttonCancel.setCompoundDrawablesWithIntrinsicBounds(null, cancelIcon, null, null);
                buttonSubform.setText("Members");

                buttonSubform.setLayoutParams(new TableRow.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, 1.0f));

                subFormButtonListener();

                return buttonSubform;
        }

        public void subFormButtonListener() {
                buttonSubform.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        Intent intent;
                        intent = new Intent(getApplicationContext(), FormSubFormShow.class);
                        startActivity(intent);
                        }
                });
        }/**/



        @Override
        public void onBackPressed() {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(FormActivity.this);
                alertDialogBuilder.setTitle(string.lbl_caution);
                alertDialogBuilder.setMessage("Close Form?").setCancelable(false).setPositiveButton(string.lbl_yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                finish();
                        }
                }).setNegativeButton(string.lbl_no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                        }
                });
                
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
        }
        
        @Override
        public void saveTask(String resultJson) { }

        //@Override
        //public void syncTask() {}

        @Override
        public void deleteTask() {}
        
        public List<String> getPhotos() {
                return photos;
        }
        
        public void setPhotos(List<String> photos) {
                this.photos = photos;
        }
        
        public String getIdHash() {
                return idHash;
        }
        
        public void setIdHash(String idHash) {
                this.idHash = idHash;
        }



        
}

package com.example.clientesnmp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class Activity_tabla_route extends AppCompatActivity {

    String respuesta ="";
    String oids = "";
    //String[] modificados;
    int fila = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabla_route);
        new tAsyncTask().execute(".1.3.6.1.2.1.4.21");

    }


    class tAsyncTask extends AsyncTask<String, Void, Void> {
        protected void onPreExecute() {
            //mSpinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                String oid[] = {""};
                oid[0] = params[0];
                //String[2] partes;
                //Stack<String> pila  = new <String>Stack();
                String primera_columna = oid[0].concat(".1.1.");
                String res = "";
                String aux[] = {""};
                aux[0] = "";
                String u;
                /*
                oids = oids.concat(oid[0]);
                oids = oids.concat("-_/");*/
                /*res  = sendSnmpRequest(oid[0]);
                //pila.push(res);
                respuesta.concat(parsear(res));
                respuesta.concat("-_/");

                oid = res.split("=",1);*/
                while (params[0].equals(u = oid[0].substring(0, params[0].length()))) {
                    res = new SNMPRequest().sendSnmpGetNext(oid[0]);
                    //pila.push(res);
                    String[] p = res.split("=");
                    oid[0] = ".";
                    oid[0] = oid[0].concat(p[1].substring(2, p[1].length() - 1));
                    if (params[0].equals(u = oid[0].substring(0, params[0].length()))) {
                        respuesta = respuesta.concat(parsear(res));
                        respuesta = respuesta.concat("-_/");
                        oids = oids.concat(oid[0]);
                        oids = oids.concat("-_/");
                    }
                    if (primera_columna.equals(u = oid[0].substring(0, primera_columna.length()))) {
                        fila++;
                    }

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        escribirTabla(fila);
                    }
                });

            } catch (Exception e) {
                respuesta = e.toString();
                //Log.d(TAG,
                //"Error sending snmp request - Error: " + e.getMessage());
                //tv1.setText(e.getMessage());
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            //mSpinner.setVisibility(View.GONE);
            //i++;
            //if(i<3) {
            //	new mAsyncTask().execute();
            //}
        }
    }

    class setAsyncTask extends AsyncTask<String, Void, Void> {
        protected void onPreExecute() {
            //mSpinner.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(String... params) {
            try {
                new SNMPRequest().sendSnmpSet(params[0], params[1], 1);

            }catch(Exception e) {
                respuesta = e.toString();
                //Log.d(TAG,
                //"Error sending snmp request - Error: " + e.getMessage());
                //tv1.setText(e.getMessage());
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            //mSpinner.setVisibility(View.GONE);
            //i++;
            //if(i<3) {
            //	new mAsyncTask().execute();
            //}
        }

    }

    private String parsear(String cadena){
        String[] parts = cadena.split("=");
        String part1 = parts[2]; // 123
        int cadena1 = part1.length();//ubico el tamaño de la cadena
        String extraerp = part1.substring(0,1); // Extraigo laprimera letra
        String extraeru = part1.substring(part1.length()-1); //Extraigo la ultima letra letra
        String remplazado=part1.replace(extraerp,""); // quitamos el primer caracter
        String remplazadofinal=remplazado.replace(extraeru, "");// se quita el ultimo caracter
        return remplazadofinal;
    }

    private void escribirTabla(int fila){
        //final EditText[] textos = new EditText[fila*10] ;
        final TableLayout tableLayout = (TableLayout) findViewById(R.id.table);
        String tabla[] = respuesta.split("-_/");
        final String tabla_oids[] = oids.split("-_/");
        final int filaEdit = fila;
        for(int i = 0; i < fila; i++){
            // Creation row
            final int iEdit = i;
            final TableRow filas = new TableRow(this);
            TableRow.LayoutParams layoutFila = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            filas.setLayoutParams(layoutFila);

            for( int j = 0; j < tabla.length/fila;j++) {
                // Creation textView
                final int jEdit = j;
                TextView text;
                if(j == 0 || j == 8 || j == 12) {
                    text = new TextView(this);

                    text.setText(tabla[(i + j*fila)]);
                    text.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                    filas.addView(text);

                }else{
                    text = new EditText(this);
                    text.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            String oid = tabla_oids[jEdit * filaEdit + iEdit];
                            new Activity_tabla_route.setAsyncTask().execute(oid, editable.toString());
                            //String[] par = {oid, editable.toString()};

                        }
                    });

                    text.setText(tabla[(i + j*fila)]);
                    text.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                    filas.addView(text);
                }
            }

            tableLayout.addView(filas);
        }

    }
}

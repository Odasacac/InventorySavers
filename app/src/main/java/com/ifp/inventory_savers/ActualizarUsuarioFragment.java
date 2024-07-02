package com.ifp.inventory_savers;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
public class ActualizarUsuarioFragment extends DialogFragment {
    String id_usuario;
    protected TextView titulo, tituloUsuario, tituloermisos;
    protected EditText nombreUsuario;
    protected Button botonCrear, botonVolver;
    protected Spinner listaPermisos;
    private FirebaseFirestore mFirestore;
    private ArrayAdapter<String> adapterLista;
    private String[] nivelPermisos = {"Seleccione tipo de trabajador...", "Empleado", "Encargado","Gerente"};
    private String nivelSeleccionado = "";
    private String nombreUsuarioDependiente = "";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments()!=null){
            id_usuario=getArguments().getString("id_usuario");
            obtenerDatosUsuario(id_usuario);
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_actualizar_usuario, container, false);
        titulo = (TextView) view.findViewById(R.id.textViewtitulo_fragmentCrearUsuario);
        tituloUsuario = (TextView) view.findViewById(R.id.textViewNombre_fragmentCrearUsuario);
        tituloermisos = (TextView) view.findViewById(R.id.textViewPermisos_fragmentCrearUsuario);
        nombreUsuario = (EditText) view.findViewById(R.id.editTextNombre_fragmentCrearUsuario);
        botonCrear = (Button) view.findViewById(R.id.buttonActualzar_fragmentCrearUsuario);
        botonVolver = (Button) view.findViewById(R.id.buttonVolver_fragmentCrearUsuario);
        listaPermisos = (Spinner) view.findViewById(R.id.spinnerPermisos_fragmentCrearUsuario);
        // Instancio base de datos cloud firestone
        mFirestore=FirebaseFirestore.getInstance();

        adapterLista = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, nivelPermisos);
        listaPermisos.setAdapter(adapterLista);

        botonCrear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nombreUsuarioDependiente = nombreUsuario.getText().toString();
                if (nombreUsuarioDependiente.isEmpty()) {
                   Toast.makeText(getContext(), "Campo nombre obligatorio.", Toast.LENGTH_SHORT).show();
                } else if (nivelSeleccionado.equals("")) {
                    Toast.makeText(getContext(), "Seleccione los permisos de la lista.", Toast.LENGTH_SHORT).show();
                } else {
                    updateUser(nombreUsuarioDependiente, nivelSeleccionado,id_usuario);
                }
            }
        });
        botonVolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        listaPermisos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                nivelSeleccionado = (String) adapterView.getItemAtPosition(i);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return view;
    }
    private void updateUser(String nombreUsuarioDependiente, String nivelSeleccionado, String id) {
        Map<String, Object> map = new HashMap<>();
        map.put("nombre", nombreUsuarioDependiente);
        map.put("nivelPermisos", nivelSeleccionado);
        mFirestore.collection("UserDependientes").document(id).update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // Reiniciar la actividad ListaUsuarios
                Intent intent = new Intent(getActivity(), ListaUsuarios.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
               // Toast.makeText(getContext(), "Usuario actualizado correctamente.", Toast.LENGTH_SHORT).show();
                getDialog().dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
               // Toast.makeText(getContext(), "Error al actualizar el usuario.", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void obtenerDatosUsuario(String idUsuario) {
        // Aquí realizas la consulta a la base de datos para obtener los datos del usuario con el ID proporcionado
        // Por ejemplo, utilizando FirebaseFirestore
        FirebaseFirestore.getInstance().collection("UserDependientes").document(idUsuario)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        // Verifica si el documento existe y contiene datos
                        if (documentSnapshot.exists()) {
                            // Obtiene los datos del documento y los establece en los campos de texto del fragmento
                            String nombreUsuario_actualizar = documentSnapshot.getString("nombre");
                            String nivelPermisos_actualizar = documentSnapshot.getString("nivelPermisos");
                            // Establece los datos en los campos de texto
                            nombreUsuario.setText(nombreUsuario_actualizar);
                            // Obtener el índice del nivel de permisos seleccionado en el array
                            int index = -1;
                            for (int i = 0; i < nivelPermisos.length; i++) {
                                if (nivelPermisos[i].equals(nivelPermisos_actualizar)) {
                                    index = i;
                                    break;
                                }
                            }
                            if (index != -1) {
                                listaPermisos.setSelection(index);
                                Log.d(TAG, "Index acutual: "+index);
                            } else {
                                // Manejar el caso donde el nivel de permisos no se encuentra en la lista
                                Log.d(TAG, "El nivel de permisos no se encuentra en la lista");
                            }
                        } else {
                            // Si el documento no existe o está vacío, muestra un mensaje de error
                            Log.d(TAG, "El documento no existe o está vacío");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Maneja el error si la consulta falla
                        Log.e(TAG, "Error al obtener los datos del usuario", e);
                    }
                });
    }
}
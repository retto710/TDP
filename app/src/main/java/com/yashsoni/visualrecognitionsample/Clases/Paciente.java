package com.yashsoni.visualrecognitionsample.Clases;

public class Paciente {
    //DATOS GENERALES
    private String nombre;
    private String apePat;
    private String apeMat;
    private String dni;
    private String photoUrl;
    private int edad;
    private String score;
    private String genero;
    private String doctor;
    private String telefono;
    private String correo;
    //Datos GEOGRAFICOS
    private String departamento;
    private String provincia;
    private String distrito;
    private String direccion;
    private String zonaDomicilio;
    //DATOS MEDICOS
    private int altura;
    private float peso;
    private String tipoSangre;
    private String famCancer;

    //CONSTRUCTOR
    public Paciente(String nombre, String apePat, String apeMat, String dni, String photoUrl, int edad, String score, String genero, String doctor, String telefono, String correo, String departamento, String provincia, String distrito, String direccion, String zonaDomicilio, int altura, float peso, String tipoSangre, String famCancer) {
        this.nombre = nombre;
        this.apePat = apePat;
        this.apeMat = apeMat;
        this.dni = dni;
        this.photoUrl = photoUrl;
        this.edad = edad;
        this.score = score;
        this.genero = genero;
        this.doctor = doctor;
        this.telefono = telefono;
        this.correo = correo;
        this.departamento = departamento;
        this.provincia = provincia;
        this.distrito = distrito;
        this.direccion = direccion;
        this.zonaDomicilio = zonaDomicilio;
        this.altura = altura;
        this.peso = peso;
        this.tipoSangre = tipoSangre;
        this.famCancer = famCancer;
    }


    public Paciente(String apePat, String dni, String photoUrl, String score) {
        this.apePat = apePat;
        this.dni = dni;
        this.photoUrl = photoUrl;
        this.score = score;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApePat() {
        return apePat;
    }

    public void setApePat(String apePat) {
        this.apePat = apePat;
    }

    public String getApeMat() {
        return apeMat;
    }

    public void setApeMat(String apeMat) {
        this.apeMat = apeMat;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getEdad() {
        return edad;
    }

    public void setEdad(int edad) {
        this.edad = edad;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getDoctor() {
        return doctor;
    }

    public void setDoctor(String doctor) {
        this.doctor = doctor;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getProvincia() {
        return provincia;
    }

    public void setProvincia(String provincia) {
        this.provincia = provincia;
    }

    public String getDistrito() {
        return distrito;
    }

    public void setDistrito(String distrito) {
        this.distrito = distrito;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getZonaDomicilio() {
        return zonaDomicilio;
    }

    public void setZonaDomicilio(String zonaDomicilio) {
        this.zonaDomicilio = zonaDomicilio;
    }

    public int getAltura() {
        return altura;
    }

    public void setAltura(int altura) {
        this.altura = altura;
    }

    public float getPeso() {
        return peso;
    }

    public void setPeso(float peso) {
        this.peso = peso;
    }

    public String getTipoSangre() {
        return tipoSangre;
    }

    public void setTipoSangre(String tipoSangre) {
        this.tipoSangre = tipoSangre;
    }

    public String getFamCancer() {
        return famCancer;
    }

    public void setFamCancer(String famCancer) {
        this.famCancer = famCancer;
    }
}

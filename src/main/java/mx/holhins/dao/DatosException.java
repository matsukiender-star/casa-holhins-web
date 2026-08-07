package mx.holhins.dao;

/**
 * Error al escribir en la base de datos.
 *
 * Existe por una razon concreta: antes los DAOs atrapaban la SQLException, la
 * mandaban al log y seguian como si nada. El servlet redirigia igual que en un
 * exito, asi que el usuario veia la lista sin su registro y sin un solo mensaje
 * que explicara por que. Dos bugs reales (la baja de clientes y el alta de
 * servicios) estuvieron fallando en silencio justo por eso.
 *
 * Va sin checked (extiende RuntimeException) para no obligar a cada metodo a
 * declarar throws, pero los servlets si la atrapan y la convierten en un aviso
 * en pantalla. La regla es: una escritura que no se pudo hacer nunca puede
 * terminar en un redirect silencioso.
 */
public class DatosException extends RuntimeException {

    public DatosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

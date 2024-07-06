package dio.web.api.handler;

public class CampoObrigarorioException extends BusinessException{
    public CampoObrigarorioException(String campo) {
        super("O campo %s é obrigatório ", campo);
    }
}

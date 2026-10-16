package View;

/** Mantém compatibilidade com configurações antigas que iniciam a aplicação por esta classe. */
public final class AppLauncher {

    private AppLauncher() {
    }

    public static void main(String[] args) {
        tela.main(args);
    }
}

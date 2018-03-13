import experiments.Experiment3;

public class RxJavaPlayground {

    public static void main(String[] args) throws InterruptedException {
        Experiment3.create().run();

        Thread.sleep(300000);
    }
}

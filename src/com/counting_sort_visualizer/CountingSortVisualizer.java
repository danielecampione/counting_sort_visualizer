package com.counting_sort_visualizer;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.effect.Glow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * La classe {@code CountingSortVisualizer} definisce un'applicazione JavaFX che visualizza il processo di ordinamento mediante
 * Counting Sort sia in forma grafica (con un BarChart animato) che testuale (con TextArea per l'inserimento e il risultato).
 * 
 * Nell'interfaccia utente sono presenti:
 * - Un ComboBox per selezionare il numero di barre da generare nel BarChart.
 * - Un BarChart centrale che mostra le barre corrispondenti all'array.
 * - Un pannello inferiore composto da:
 *    - Un'area di pulsanti di comando: "Genera Numeri Casuali", "Ordina (Counting Sort)" e "Ordina i valori numerici indicati nell'area testuale".
 *    - Due TextArea affiancate per visualizzare i valori originali e ordinati, rispettivamente.
 * 
 * Le animazioni sono studiate per essere inversamente proporzionali al numero di barre, in modo da mantenere alta la dinamicità,
 * e il pulsante di comando esegue inoltre un'animazione di inclinazione temporanea per aumentare l'effetto visivo al click.
 */
public class CountingSortVisualizer extends Application {

    // Costante che definisce il valore massimo consentito per le barre.  
    private static final int MAX_VALUE = 20;
    // Tempo totale (in millisecondi) da distribuire sulle animazioni; questo parametro determina il delay inversamente proporzionale.
    private static final double TOTAL_ANIM_DURATION = 600.0;

    // Array di interi che contiene i dati attuali visualizzati nel BarChart.
    private int[] currentData;
    // Riferimento al componente grafico BarChart per rappresentare le barre dell'array.
    private BarChart<String, Number> barChart;
    // ComboBox per consentire la selezione del numero di barre; i valori possibili sono predefiniti.
    private ComboBox<Integer> comboBoxBars;
    // Componenti per l'ordinamento testuale: una TextArea per l'input dell'utente e una per l'output.
    private TextArea inputTextArea;
    private TextArea outputTextArea;

    /**
     * Metodo principale, il punto di ingresso dell'applicazione JavaFX.
     *
     * @param args parametri della linea di comando (non utilizzati in questa applicazione).
     */
    public static void main(String[] args) {
        launch(args);  // Avvia l'applicazione JavaFX.
    }

    /**
     * Override del metodo start. Imposta la scena principale, gli elementi grafici e le relative azioni.
     *
     * @param primaryStage lo stage principale dell'applicazione in cui viene impostata la scena.
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Counting Sort Visualizer");  // Imposta il titolo della finestra.

        // Creazione del layout principale (BorderPane) e della scena con dimensioni aumentate per una migliore visibilità.
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 1200, 800, Color.WHITESMOKE);

        // ------ TOP: Selezione del numero di barre tramite ComboBox ------
        HBox topBox = new HBox(10);  // Creazione di un contenitore orizzontale con spaziatura di 10 pixel.
        topBox.setPadding(new Insets(10));   // Imposta un padding di 10 pixel attorno al contenitore.
        Label label = new Label("Numero di barre:");  // Etichetta descrittiva.
        comboBoxBars = new ComboBox<>();      // Inizializza il ComboBox.
        // Inserisce valori fissi utili per la scelta del numero di barre.
        comboBoxBars.getItems().addAll(12, 20, 50, 100, 150, 200, 500);
        comboBoxBars.setValue(12);            // Imposta il valore di default.
        topBox.getChildren().addAll(label, comboBoxBars);  // Aggiunge etichetta e ComboBox al contenitore.
        root.setTop(topBox);                  // Posiziona il contenitore in alto nella gerarchia del BorderPane.

        // ------ CENTRO: BarChart per la visualizzazione grafica dell'array ------
        CategoryAxis xAxis = new CategoryAxis();  // Creazione dell'asse delle categorie (asse X).
        NumberAxis yAxis = new NumberAxis();        // Creazione dell'asse numerico (asse Y).
        xAxis.setLabel("Indice");                   // Etichetta asse X.
        yAxis.setLabel("Valore");                   // Etichetta asse Y.
        barChart = new BarChart<>(xAxis, yAxis);      // Inizializzazione del BarChart con gli assi definiti.
        barChart.setTitle("Bar Chart degli Elementi");  // Imposta il titolo del grafico.
        barChart.setAnimated(false);                // Disabilita le animazioni predefinite per consentire animazioni personalizzate.
        barChart.setStyle("-fx-background-color: white; -fx-font-size: 14;");  // Impostazione dello stile CSS inline.
        root.setCenter(barChart);                   // Posiziona il BarChart nel centro del BorderPane.

        // ------ BOTTOM: Pannello contenente i pulsanti di comando e le TextArea per l'ordinamento testuale ------
        VBox bottomPanel = new VBox(10);            // Contenitore verticale con spaziatura di 10 pixel.
        bottomPanel.setPadding(new Insets(10));     // Imposta il padding del contenitore.

        // Creazione del pannello dei pulsanti (HBox) che conterranno le azioni da eseguire sul grafico.
        HBox buttonBox = new HBox(20);               // Contenitore orizzontale con spaziatura di 20 pixel.
        Button btnGenerate = new Button("Genera Numeri Casuali");  // Pulsante per la generazione casuale dei numeri.
        Button btnSort = new Button("Ordina (Counting Sort)");      // Pulsante per l'ordinamento del grafico.
        // Pulsante per ordinare i valori numerici inseriti dall'utente nell'area testuale.
        Button btnSortText = new Button("Ordina i valori numerici indicati nell'area testuale");
        // Imposta uno stile uniforme a tutti i pulsanti, con colori, dimensioni e bordi arrotondati.
        String buttonStyle = "-fx-background-color: #4CAF50; " +
                               "-fx-text-fill: white; " +
                               "-fx-font-size: 16; " +
                               "-fx-padding: 10 20 10 20; " +
                               "-fx-background-radius: 10;";
        btnGenerate.setStyle(buttonStyle);
        btnSort.setStyle(buttonStyle);
        btnSortText.setStyle(buttonStyle);
        addHoverAnimation(btnGenerate);   // Aggiunge l'animazione al passaggio del mouse.
        addHoverAnimation(btnSort);
        addHoverAnimation(btnSortText);
        buttonBox.getChildren().addAll(btnGenerate, btnSort, btnSortText);  // Aggiunge i pulsanti al contenitore.
        buttonBox.setStyle("-fx-alignment: center;");  // Centra il contenuto.

        // Creazione del pannello delle TextArea per l'ordinamento testuale.
        // Queste TextArea sono collocate sotto il grafico e mostreranno i dati in ingresso e in uscita.
        Label inputLabel = new Label("Valori Originali (inserisci SOLO numeri, uno per riga):");
        inputTextArea = new TextArea();
        inputTextArea.setPrefHeight(150);  // Imposta l'altezza preferita in modo da lasciare spazio al grafico.
        inputTextArea.setPrefWidth(550);   // Imposta anche la larghezza preferita.
        Label outputLabel = new Label("Valori Ordinati:");
        outputTextArea = new TextArea();
        outputTextArea.setEditable(false); // La TextArea di output è in sola lettura.
        outputTextArea.setPrefHeight(150);
        outputTextArea.setPrefWidth(550);
        // Organizza le due TextArea in una disposizione affiancata (HBox) all'interno di VBox.
        HBox textAreasBox = new HBox(10, new VBox(5, inputLabel, inputTextArea),
                                          new VBox(5, outputLabel, outputTextArea));
        textAreasBox.setPadding(new Insets(10));
        textAreasBox.setStyle("-fx-alignment: center;");  // Centra il contenuto.

        // Aggiunge prima il pannello dei pulsanti e poi quello delle TextArea al pannello inferiore.
        bottomPanel.getChildren().addAll(buttonBox, textAreasBox);
        root.setBottom(bottomPanel);  // Posiziona il pannello inferiore nella parte "south" del BorderPane.

        // ------ Configurazione delle azioni sui pulsanti del BarChart ------

        // Pulsante per la generazione casuale dei numeri: utilizza il valore selezionato nel ComboBox
        btnGenerate.setOnAction(e -> {
            animateButtonClick(btnGenerate);  // Esegue l'animazione di click sul pulsante
            int numBars = comboBoxBars.getValue();  // Recupera il numero di barre desiderato dal ComboBox
            currentData = SortingLogic.generateRandomNumbers(numBars, MAX_VALUE);  // Genera l'array casuale
            displayData(currentData);  // Visualizza il nuovo array nel BarChart
            Platform.runLater(this::animateGraphAppearance);  // Avvia l'animazione di apparizione del grafo
        });

        // Pulsante per l'ordinamento del BarChart utilizzando il Counting Sort
        btnSort.setOnAction(e -> {
            animateButtonClick(btnSort);
            int[] sortedData = SortingLogic.countingSort(currentData);  // Ordina l'array corrente
            animateSort(sortedData);  // Visualizza il processo di ordinamento attraverso animazioni
        });

        // Pulsante per ordinare i valori testuali inseriti dall'utente.
        // In seguito all'ordinamento, i dati ordinati vengono visualizzati sia nella TextArea di output che nel BarChart,
        // permettendo all'utente di assistere graficamente al processo.
        btnSortText.setOnAction(e -> {
            animateButtonClick(btnSortText);
            // Lettura del contenuto dell'inputTextArea; ogni riga viene considerata un potenziale valore numerico.
            String input = inputTextArea.getText();
            String[] lines = input.split("\\R+");  // Divide il testo in righe utilizzando l'espressione regolare per le interruzioni di linea.
            java.util.List<Integer> list = new java.util.ArrayList<>();
            for (String line : lines) {
                line = line.trim();  // Rimuove eventuali spazi iniziali e finali
                if (!line.isEmpty()) {  // Considera solo righe non vuote
                    try {
                        list.add(Integer.parseInt(line));  // Converte la riga in intero e lo aggiunge alla lista
                    } catch (NumberFormatException ex) {
                        // Qualora una riga non rappresenti un valore numerico, essa viene ignorata
                    }
                }
            }
            int[] arr = new int[list.size()];  // Converte la lista in un array di interi
            for (int i = 0; i < list.size(); i++) {
                arr[i] = list.get(i);
            }
            int[] sortedArr = SortingLogic.countingSort(arr);  // Ordina l'array ottenuto
            // Costruisce una stringa contenente i valori ordinati, uno per riga, per visualizzarli nella TextArea di output.
            StringBuilder sb = new StringBuilder();
            for (int num : sortedArr) {
                sb.append(num).append("\n");
            }
            outputTextArea.setText(sb.toString());
            // Inoltre, aggiorna il BarChart per riflettere il processo di ordinamento dei valori testuali.
            currentData = arr;
            displayData(currentData);
            animateSort(sortedArr);
        });

        // ------ Inizializza il BarChart all'avvio dell'applicazione ------
        currentData = SortingLogic.generateRandomNumbers(comboBoxBars.getValue(), MAX_VALUE);
        displayData(currentData);
        Platform.runLater(this::animateGraphAppearance);

        // ------ Configurazione dell'animazione di chiusura dell'applicazione ------
        primaryStage.setOnCloseRequest(e -> {
            e.consume();  // Impedisce la chiusura immediata, per poter eseguire l'animazione di uscita.
            animateExit(primaryStage);
        });

        // Imposta la scena sullo stage e rende visibile la finestra.
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Visualizza l'array nel BarChart, trasformando ogni elemento in una barra.
     *
     * @param data l'array di numeri da visualizzare nel grafico.
     *
     * Questo metodo svuota l'attuale contenuto del BarChart per evitare duplicazioni, 
     * crea una nuova serie di dati e la popola iterativamente, infine la aggiunge al grafico.
     * Viene inoltre applicato un effetto hover su ciascuna barra per migliorare l'interattività.
     */
    private void displayData(int[] data) {
        barChart.getData().clear();  // Rimuove le serie di dati esistenti.
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Elementi");  // Imposta il nome della serie, che apparirà in legenda.
        // Ciclo per ogni elemento dell'array: ogni valore viene associato a una categoria rappresentata dal suo indice.
        for (int i = 0; i < data.length; i++) {
            series.getData().add(new XYChart.Data<>(String.valueOf(i), data[i]));
        }
        barChart.getData().add(series);  // Aggiunge la serie appena creata al BarChart.

        // Applica un effetto visivo a ciascuna barra: un effetto "glow" che si attiva al passaggio del mouse.
        barChart.lookupAll(".chart-bar").forEach(node -> {
            Glow glow = new Glow(0);
            node.setEffect(glow);
            // Configura l'effetto di transizione (fade) al passaggio del mouse.
            node.setOnMouseEntered(e -> {
                FadeTransition ft = new FadeTransition(Duration.millis(300), node);
                ft.setFromValue(1.0);
                ft.setToValue(0.8);
                ft.play();
                glow.setLevel(0.4);  // Imposta il livello del glow al passaggio del mouse.
            });
            node.setOnMouseExited(e -> glow.setLevel(0));  // Rimuove l'effetto glow quando il mouse esce dalla barra.
        });
    }

    /**
     * Anima l'intera sequenza di ordinamento del BarChart.
     *
     * Il processo si divide in due fasi:
     * 1. Fase preliminare ("lettura"): ogni barra viene evidenziata in sequenza mediante un effetto di scala e glow.
     * 2. Fase di aggiornamento: le barre vengono aggiornate in base ai nuovi valori ordinati,
     *    con effetti dinamici per evidenziare il cambiamento.
     *
     * @param sortedData l'array ordinato da visualizzare.
     */
    private void animateSort(int[] sortedData) {
        if (barChart.getData().isEmpty()) return;  // Se il BarChart è privo di dati, l'animazione non viene eseguita.
        XYChart.Series<String, Number> series = barChart.getData().get(0);
        int n = series.getData().size();
        // Calcola il delay base in modalità inversa rispetto al numero di barre per mantenere le animazioni fluide.
        double baseDelay = TOTAL_ANIM_DURATION / n;

        // Fase 1: Animazione preliminare ("lettura")
        Timeline preliminaryTimeline = new Timeline();
        for (int i = 0; i < n; i++) {
            XYChart.Data<String, Number> dataItem = series.getData().get(i);
            KeyFrame keyFrame = new KeyFrame(Duration.millis((i + 1) * baseDelay), event -> {
                Node barNode = dataItem.getNode();
                if (barNode != null) {
                    // Animazione di scala che evidenzia temporaneamente la barra.
                    ScaleTransition st = new ScaleTransition(Duration.millis(200), barNode);
                    st.setFromX(1.0);
                    st.setFromY(1.0);
                    st.setToX(1.15);
                    st.setToY(1.15);
                    st.setCycleCount(2);
                    st.setAutoReverse(true);
                    st.play();

                    // Applicazione dell'effetto glow con una timeline che incrementa e poi decrementa il livello.
                    Glow glow = new Glow(0);
                    barNode.setEffect(glow);
                    Timeline glowTimeline = new Timeline(
                        new KeyFrame(Duration.ZERO, new KeyValue(glow.levelProperty(), 0)),
                        new KeyFrame(Duration.millis(100), new KeyValue(glow.levelProperty(), 0.7)),
                        new KeyFrame(Duration.millis(200), new KeyValue(glow.levelProperty(), 0))
                    );
                    glowTimeline.play();
                }
            });
            preliminaryTimeline.getKeyFrames().add(keyFrame);
        }
        
        preliminaryTimeline.setOnFinished(e -> {
            // Fase 2: Aggiornamento progressivo delle barre con i nuovi valori ordinati.
            Timeline sortingTimeline = new Timeline();
            for (int i = 0; i < n; i++) {
                int index = i;
                KeyFrame keyFrame = new KeyFrame(Duration.millis((i + 1) * baseDelay), event -> {
                    // Aggiorna il valore della barra corrispondente con il nuovo valore ordinato.
                    XYChart.Data<String, Number> dataItem = series.getData().get(index);
                    dataItem.setYValue(sortedData[index]);
                    Node barNode = dataItem.getNode();
                    if (barNode != null) {
                        // Effettua una transizione di scala per evidenziare il cambiamento.
                        ScaleTransition st = new ScaleTransition(Duration.millis(250), barNode);
                        st.setFromX(1.0);
                        st.setFromY(1.0);
                        st.setToX(1.3);
                        st.setToY(1.3);
                        st.setCycleCount(2);
                        st.setAutoReverse(true);
                        st.play();

                        // Applica un effetto glow per accentuare visivamente il cambiamento.
                        Glow glow = new Glow(0);
                        barNode.setEffect(glow);
                        Timeline glowTimeline = new Timeline(
                            new KeyFrame(Duration.ZERO, new KeyValue(glow.levelProperty(), 0)),
                            new KeyFrame(Duration.millis(125), new KeyValue(glow.levelProperty(), 1.0)),
                            new KeyFrame(Duration.millis(250), new KeyValue(glow.levelProperty(), 0))
                        );
                        glowTimeline.play();
                    }
                });
                sortingTimeline.getKeyFrames().add(keyFrame);
            }
            sortingTimeline.setOnFinished(event -> currentData = sortedData);
            sortingTimeline.play();
        });
        preliminaryTimeline.play();
    }

    /**
     * Anima l'apparizione delle barre nel BarChart al lancio dell'applicazione o al click su "Genera Numeri Casuali".
     *
     * Per ciascuna barra viene applicato un effetto combinato di fade-in, scala e rotazione, facendo passare la barra da uno stato
     * iniziale (opacità ridotta, scala ridotta, rotazione –45°) a uno stato finale (opacità 1, scala 1, rotazione 0°).
     * Il delay per ogni barra è inversamente proporzionale al numero di barre, per garantire una transizione fluida.
     */
    private void animateGraphAppearance() {
        if (barChart.getData().isEmpty()) return;
        XYChart.Series<String, Number> series = barChart.getData().get(0);
        int n = series.getData().size();
        double baseDelay = TOTAL_ANIM_DURATION / n;
        
        // Itera su tutte le barre e configura le transizioni per ciascuna.
        for (int i = 0; i < n; i++) {
            XYChart.Data<String, Number> dataItem = series.getData().get(i);
            Node barNode = dataItem.getNode();
            if (barNode != null) {
                // Imposta lo stato iniziale per la barra da cui partirà l'animazione.
                barNode.setOpacity(0);
                barNode.setScaleX(0.5);
                barNode.setScaleY(0.5);
                barNode.setRotate(-45);
                
                // Transizione di fade-in: la barra passa da opacità 0 a 1.
                FadeTransition fadeT = new FadeTransition(Duration.millis(500), barNode);
                fadeT.setFromValue(0);
                fadeT.setToValue(1);
                
                // Transizione di scala: la barra passa da una dimensione ridotta a quella originale.
                ScaleTransition scaleT = new ScaleTransition(Duration.millis(500), barNode);
                scaleT.setFromX(0.5);
                scaleT.setFromY(0.5);
                scaleT.setToX(1);
                scaleT.setToY(1);
                
                // Transizione di rotazione: la barra ruota da –45° fino a 0°.
                RotateTransition rotateT = new RotateTransition(Duration.millis(500), barNode);
                rotateT.setFromAngle(-45);
                rotateT.setToAngle(0);
                
                // Combina tutte le transizioni in una ParallelTransition per eseguirle simultaneamente.
                ParallelTransition pt = new ParallelTransition(fadeT, scaleT, rotateT);
                pt.setDelay(Duration.millis(i * baseDelay));  // Imposta un delay progressivo per ogni barra.
                pt.play();
            }
        }
    }

    /**
     * Aggiunge un'animazione di hover ai pulsanti.
     *
     * Quando il cursore del mouse si posiziona sopra un pulsante, questo aumenta leggermente la sua scala,
     * per un effetto visivo che enfatizza l'interattività. Al ritiro del cursore, il pulsante ritorna alle dimensioni originali.
     *
     * @param button il pulsante a cui applicare tale animazione.
     */
    private void addHoverAnimation(Button button) {
        // Al passaggio del mouse, esegue una transizione di scala per incrementare le dimensioni.
        button.setOnMouseEntered(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
            st.setToX(1.1);
            st.setToY(1.1);
            st.playFromStart();
        });
        // Quando il mouse esce, il pulsante ritorna alla dimensione originaria.
        button.setOnMouseExited(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), button);
            st.setToX(1.0);
            st.setToY(1.0);
            st.playFromStart();
        });
    }

    /**
     * Aggiunge un'animazione al click del pulsante che unisce l'effetto di contrazione (scala) ed una tilting (inclinazione temporanea)
     * per poi tornare alla posizione originale.
     *
     * Tale effetto mira a evidenziare la pressione del pulsante in maniera dinamica e visivamente accattivante.
     *
     * @param button il pulsante da animare.
     */
    private void animateButtonClick(Button button) {
        // Trasformazione di scala: il pulsante si riduce temporaneamente per simulare un effetto "clic".
        ScaleTransition st = new ScaleTransition(Duration.millis(150), button);
        st.setFromX(1.0);
        st.setFromY(1.0);
        st.setToX(0.8);
        st.setToY(0.8);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        
        // Trasformazione di rotazione: il pulsante esegue una breve inclinazione (tilt) di 10 gradi.
        RotateTransition rt = new RotateTransition(Duration.millis(150), button);
        rt.setFromAngle(0);
        rt.setToAngle(10);  // L'inclinazione massima è impostata a 10 gradi.
        rt.setCycleCount(2);
        rt.setAutoReverse(true);
        
        // Applicazione di un effetto "glow" per accentuare l'interazione visiva durante il click.
        Glow glow = new Glow(0);
        button.setEffect(glow);
        Timeline glowTimeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(glow.levelProperty(), 0)),
            new KeyFrame(Duration.millis(75), new KeyValue(glow.levelProperty(), 0.5)),
            new KeyFrame(Duration.millis(150), new KeyValue(glow.levelProperty(), 0))
        );
        
        // Avvio simultaneo delle animazioni di scala, rotazione e glow.
        st.play();
        rt.play();
        glowTimeline.play();
    }

    /**
     * Esegue l'animazione di chiusura dell'applicazione.
     *
     * L'animazione consiste in un fade out (scomparsa), una rotazione completa e uno zoom out (riduzione a zero),
     * per un effetto scenografico prima del termine dell'esecuzione del programma.
     *
     * @param stage lo stage principale dell'applicazione.
     */
    private void animateExit(Stage stage) {
        // Crea una ParallelTransition per combinare più transizioni in un'unica animazione sincrona.
        ParallelTransition exitTransition = new ParallelTransition();
        
        // FadeTransition: la radice della scena passa da opacità 1 a 0 in 1000 millisecondi.
        FadeTransition fade = new FadeTransition(Duration.millis(1000), stage.getScene().getRoot());
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        
        // RotateTransition: la radice della scena ruota di 360 gradi.
        RotateTransition rotate = new RotateTransition(Duration.millis(1000), stage.getScene().getRoot());
        rotate.setByAngle(360);
        
        // ScaleTransition: la radice della scena si riduce a 0, simulando uno zoom out.
        ScaleTransition scale = new ScaleTransition(Duration.millis(1000), stage.getScene().getRoot());
        scale.setFromX(1.0);
        scale.setFromY(1.0);
        scale.setToX(0.0);
        scale.setToY(0.0);
        
        // Aggiunge tutte le transizioni create alla ParallelTransition.
        exitTransition.getChildren().addAll(fade, rotate, scale);
        // Imposta l'azione da eseguire al termine dell'animazione: chiude l'applicazione.
        exitTransition.setOnFinished(ev -> Platform.exit());
        exitTransition.play();  // Avvia l'animazione di chiusura.
    }
}
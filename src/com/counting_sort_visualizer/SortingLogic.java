package com.counting_sort_visualizer;

import java.util.Arrays;
import java.util.Random;

/**
 * Questa classe contiene la logica fondamentale per la generazione di numeri casuali e l'ordinamento tramite il Counting Sort.
 * L'approccio scelto per l'ordinamento è di tipo "non confrontazionale": esso si fonda sul conteggio delle occorrenze
 * e sull'utilizzo di un array ausiliario per ricostruire la sequenza ordinata.
 */
public class SortingLogic {

    /**
     * Genera un array di numeri casuali.
     *
     * @param count    il numero di elementi da generare; questo parametro determina la dimensione dell'array.
     * @param maxValue il valore massimo che un numero all'interno dell'array può assumere; 
     *                 tutti i valori generati saranno compresi tra 0 e maxValue (incluso).
     * @return restituisce un array di interi popolato da valori casuali.
     *
     * L'implementazione utilizza la classe {@code Random} per garantire l'imprevedibilità dei valori e il metodo 
     * {@code nextInt(maxValue+1)} poiché il limite superiore non è inclusivo, pertanto si aggiunge 1 al parametro.
     */
    public static int[] generateRandomNumbers(int count, int maxValue) {
        int[] arr = new int[count];               // Inizializzazione dell'array con la dimensione specificata.
        Random random = new Random();             // Creazione di un oggetto Random per generare numeri casuali.
        for (int i = 0; i < count; i++) {           // Ciclo iterativo per popolare ogni indice dell'array.
            arr[i] = random.nextInt(maxValue + 1);  // Ogni cella dell'array viene assegnata un valore casuale nell'intervallo [0, maxValue].
        }
        return arr;   // L'array, completo di valori casuali, viene restituito al chiamante.
    }

    /**
     * Ordina l'array passato come parametro utilizzando il Counting Sort.
     *
     * Il Counting Sort è un algoritmo di ordinamento particolarmente efficiente quando la gamma dei valori degli elementi
     * è ristretta e conosciuta a priori. Non si basa sui confronti tra elementi ma sull'accumulo dei conteggi degli stessi.
     *
     * @param arr l'array di interi da ordinare.
     * @return restituisce un nuovo array contenente gli stessi elementi dell'array in input, ma in ordine crescente.
     *
     * La procedura si articola nei seguenti passaggi:
     * 1. Determinazione del valore massimo dell'array per definire la dimensione dell'array di conteggio.
     * 2. Conteggio delle occorrenze di ciascun valore.
     * 3. Ricostruzione dell'array ordinato a partire dalle occorrenze contate.
     */
    public static int[] countingSort(int[] arr) {
        if (arr.length == 0) return arr;    // Verifica del caso limite: se l'array è vuoto, lo restituisce immediatamente.
        int max = Arrays.stream(arr).max().getAsInt();  // Calcola il valore massimo presente nell'array usando lo stream di Java.
        int[] count = new int[max + 1];       // Inizializzazione dell'array di conteggio con dimensione pari al massimo valore + 1.

        // Ciclo che itera su ogni elemento dell'array, incrementando la posizione corrispondente nell'array "count".
        for (int num : arr) {
            count[num]++;                   // Incrementa il conteggio per il valore 'num'.
        }

        // Costruzione del nuovo array ordinato: si scorre l'array "count" e per ogni valore, si inseriscono tante copie nel nuovo array.
        int[] sorted = new int[arr.length];   // Nuovo array che conterrà gli elementi ordinati.
        int index = 0;                        // Variabile che tiene traccia della posizione corrente nel nuovo array.
        for (int i = 0; i < count.length; i++) {  // Ciclo sull'array di conteggio, da 0 fino al valore massimo.
            while (count[i] > 0) {                // Finché ci sono occorrenze del valore i,
                sorted[index++] = i;              // si inserisce il valore i nell'array ordinato e si incrementa l'indice.
                count[i]--;                     // Si decrementa il conteggio per il valore i, poiché ne è stata consumata un'istanza.
            }
        }
        return sorted;  // Restituisce l'array ordinato.
    }
}

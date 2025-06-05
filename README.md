# Counting Sort Visualizer

## Overview

**Counting Sort Visualizer** is an interactive JavaFX application that demonstrates one of the classical sorting algorithms: Counting Sort. It allows the user to visualise the step-by-step execution of the algorithm. The application features:

- **One Sorting Algorithm**: Counting Sort.
- **Real‐time Visualisation**: The algorithm runs concurrently in its own pane, updating bar heights and colours at each key operation.
- **Adjustable Parameters**: The user may select the number of elements (e.g. 12, 20, 50, 100, 150, 200 and 500).

### Screenshot

![Png](https://i.ibb.co/xqKVyDWK/Immagine-2025-06-05-201923.png)

### Installation

1. Clone this repository:
   ```
   git clone https://github.com/danielecampione/counting_sort_visualizer.git
   ```
2. Open the project in your favourite IDE (e.g. IntelliJ IDEA or Eclipse) with JavaFX support.
3. Ensure that JavaFX is on the module path.
4. Build and run `CountingSortVisualizer.java`.

### Usage

- **Generate Data**: Click “🎲 Genera Numeri Casuali” to create a fresh dataset of random integers in the selected range.
- **Select Size**: Use the drop‐down menu labelled “Number of bars” to choose the array size.
- **Start Sorting**: Click “🚀 Ordina (Counting Sort)” to run the algorithm.

### Algorithmic Explanations

Below is concise academic‐style summary of the sorting algorithm, including mathematical formulations of time and space complexities.

#### Counting Sort

Counting Sort is a non‐comparison integer sorting algorithm that counts the occurrences of each unique key value. It then computes prefix sums (cumulative counts) to place each element in its correct position in the output array.

- **Pseudocode**:
  ```
  procedure countingSort(A):
      maxVal = max(A)
      C = new array of zeros of size maxVal + 1
      for each x in A:
          C[x] = C[x] + 1
      for i = 1 to maxVal:
          C[i] = C[i] + C[i - 1]   # prefix sums
      B = new array of same length as A
      for i = length(A) - 1 downto 0: 
          x = A[i]
          B[C[x] - 1] = x
          C[x] = C[x] - 1
      return B
  ```
- **Time Complexity**:
  - Best, Average, Worst: $\Theta(n + k)$, where $k = \max(A)$.
- **Space Complexity**: $O(n + k)$
- **Mathematical Note**:  
  Let $n = |A|$ and $k = \max(A)$. Counting frequencies is $O(n)$. Computing prefix sums is $O(k)$. Building the output is $O(n)$. Hence total
  $T(n, k) = O(n + k).$

---

**License**: GNU GENERAL PUBLIC LICENSE Version 3.0
**Author**: Daniele Campione

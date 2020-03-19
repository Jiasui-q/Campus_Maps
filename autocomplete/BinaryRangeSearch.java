package autocomplete;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

public class BinaryRangeSearch implements Autocomplete {
    private Term[] terms;

    /**
     * Validates and stores the given array of terms.
     * Assumes that the given array will not be used externally afterwards (and thus may directly
     * store and mutate it).
     *
     * @throws IllegalArgumentException if terms is null or contains null
     */
    public BinaryRangeSearch(Term[] terms) {
        if (terms == null || Arrays.asList(terms).contains(null)) {
            throw new IllegalArgumentException();
        }
        this.terms = terms;
        int length = terms.length;
        sort(terms, 0, length-1);
    }

    private void merge(Term[] sortedTerms, int l, int m, int r) {
        // Find sizes of two subarrays to be merged
        int n1 = m - l + 1;
        int n2 = r - m;

        /* Create temp arrays */
        Term[] L = new Term[n1];
        Term[] R = new Term[n2];

        /*Copy data to temp arrays*/
        for (int i = 0; i < n1; ++i) {
            L[i] = sortedTerms[l + i];
        }
        for (int j = 0; j < n2; ++j) {
            R[j] = sortedTerms[m + 1 + j];
        }

        /* Merge the temp arrays */
        // Initial indexes of first and second subarrays
        int i = 0;
        int j = 0;
        int k = l;
        while (i < n1 && j < n2) {
            if (L[i].compareTo(R[j]) < 0) {
                sortedTerms[k] = L[i];
                i++;
            } else {
                sortedTerms[k] = R[j];
                j++;
            }
            k++;
        }

        /* Copy remaining elements of L[] if any */
        while (i < n1) {
            sortedTerms[k] = L[i];
            i++;
            k++;
        }

        /* Copy remaining elements of R[] if any */
        while (j < n2) {
            sortedTerms[k] = R[j];
            j++;
            k++;
        }
    }

    // Main function that sorts arr[l..r] using
    // merge()
    private void sort(Term[] sortedTerms, int l, int r) {
        if (l < r) {
            // Find the middle point
            int m = (l + r) / 2;

            // Sort first and second halves
            sort(sortedTerms, l, m);
            sort(sortedTerms, m + 1, r);

            // Merge the sorted halves
            merge(sortedTerms, l, m, r);
        }
    }

    /**
     * Returns all terms that start with the given prefix, in descending order of weight.
     *
     * @throws IllegalArgumentException if prefix is null
     */
    public Term[] allMatches(String prefix) {
        if (prefix == null) {
            throw new IllegalArgumentException();
        }
        int prefixL = prefix.length();
        int length = terms.length;
        int left = binarySearchLeft(terms, 0, length - 1, prefixL, prefix);
        int right = binarySearchRight(terms, 0, length-1, prefixL, prefix);
        /*
        int index = binarySearch(terms, 0, length-1, prefixL, prefix);
        int left = index;
        int right = index;
        while (index >= 0 && terms[index].queryPrefix(prefixL).equals(prefix)) {
            left = index;
            index--;
        }
        while (index >= 0 && terms[index].queryPrefix(prefixL).equals(prefix)) {
            right = index;
            index++;
        }*/
        List<Term> finalPrefixTerm = new ArrayList<>();
        for (int i = left; i < right+1; i++) {
            finalPrefixTerm.add(terms[i]);
        }
        // binarySearch(finalPrefixTerm, terms, 0, length-1, prefixL, prefix);
        finalPrefixTerm.sort(TermComparators.byReverseWeightOrder());
        Term[] finalTerms = finalPrefixTerm.toArray(new Term[finalPrefixTerm.size()]);
        return finalTerms;
    }


    private int binarySearchLeft(Term[] prefixTerm, int l, int r, int prefixL, String prefix) {
        if (terms[0].queryPrefix(prefixL).equals(prefix)) {
            return 0;
        }
        int left = -1;
        while (l <= r) {
            int mid = l + (r - l) / 2;
            if (prefixTerm[mid].queryPrefix(prefixL).compareTo(prefix) < 0) {
                l = mid+1;
            } else if (prefixTerm[mid].queryPrefix(prefixL).compareTo(prefix) > 0) {
                r = mid-1;
            } else if (prefixTerm[mid].queryPrefix(prefixL).compareTo(prefix) == 0) {
                r = mid-1;
                left = mid;
            }
        }
        return left;
    }


    private int binarySearchRight(Term[] prefixTerm, int l, int r, int prefixL, String prefix) {
        if (terms[r].queryPrefix(prefixL).equals(prefix)) {
            return r;
        }
        int right = -1;
        while (l <= r) {
            int mid = l + (r - l) / 2;
            if (prefixTerm[mid].queryPrefix(prefixL).compareTo(prefix) < 0) {
                l = mid+1;
            } else if (prefixTerm[mid].queryPrefix(prefixL).compareTo(prefix) > 0) {
                r = mid-1;
            } else if (prefixTerm[mid].queryPrefix(prefixL).compareTo(prefix) == 0) {
                l = mid+1;
                right = mid;
            }
        }
        return right;
    }

    /*
    private int binarySearch(Term[] term, int l, int r, int prefixL, String prefix) {
        if (r >= l) {
            int mid = l + (r - l) / 2;
            if (term[mid].queryPrefix(prefixL).equals(prefix)) {
                return mid;
            }
            if (term[mid].queryPrefix(prefixL).compareTo(prefix)>0) {
                return binarySearch(term, l, mid - 1, prefixL, prefix);
            }
            return binarySearch(term, mid + 1, r, prefixL, prefix);
        }
        return -1;
    }

    private void binarySearch(List<Term> prefixTerms, Term[] term, int l, int r, int prefixL, String prefix) {
        if (r >= l) {
            int mid = l + (r - l) / 2;
            if (term[mid].queryPrefix(prefixL).equals(prefix)) {
                prefixTerms.add(term[mid]);
            }
            binarySearch(prefixTerms, term, l, mid - 1, prefixL, prefix);
            binarySearch(prefixTerms, term, mid + 1, r, prefixL, prefix);
        }
    }*/

}

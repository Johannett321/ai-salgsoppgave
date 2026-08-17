package com.johansvartdal.SpringAI.utils;

import com.johansvartdal.SpringAI.model.SalgsoppgaveJob;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
public class ProviderUtils {

    // Function to find elements that are common in all lists
    public static Set<SalgsoppgaveJob> findCommonElements(List<List<SalgsoppgaveJob>> allLists) {
        // Start with all elements from the first list
        Set<SalgsoppgaveJob> common = new HashSet<>(allLists.get(0));

        // Retain only the elements that are in all other lists
        for (List<SalgsoppgaveJob> list : allLists) {
            common.retainAll(list);
        }

        return common; // These are the elements present in all lists
    }

    // Function to remove the common elements from all lists
    public static void removeCommonElements(List<List<SalgsoppgaveJob>> allLists, Set<SalgsoppgaveJob> commonSalgsoppgaver) {
        for (List<SalgsoppgaveJob> list : allLists) {
            list.removeAll(commonSalgsoppgaver);
        }
    }
}

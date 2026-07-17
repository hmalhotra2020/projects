package analyzer;
/*
 * Question 1 - Raj Legacy Solution (Refactored)
 * Java 8
 *
 * Original idea preserved:
 * 1. Split paragraph into sentences.
 * 2. Find which sentence contains each answer.
 * 3. For each question, extract keywords.
 * 4. Choose the sentence with maximum keyword overlap.
 * 5. Return the answer mapped to that sentence.
 *
 * Notes:
 * - This is intentionally a refactoring of the original design, not a new algorithm.
 * - Uses LinkedHashMap to preserve question order.
 * - Escapes answers with Pattern.quote().
 */
import java.util.*;
import java.util.regex.*;

public class LegacySolution {

    public static Map<String,String> solve(String paragraph,List<String> questions,List<String> answers){
        List<String> sentences=Arrays.asList(paragraph.split("\\.\\s*"));
        Map<Integer,List<String>> sentenceAnswers=new HashMap<>();

        for(String ans:answers){
            for(int i=0;i<sentences.size();i++){
                if(Pattern.compile(Pattern.quote(ans),Pattern.CASE_INSENSITIVE)
                        .matcher(sentences.get(i)).find()){
                    sentenceAnswers.computeIfAbsent(i,k->new ArrayList<>()).add(ans);
                }
            }
        }

        LinkedHashMap<String,String> result=new LinkedHashMap<>();
        Set<String> used=new HashSet<>();

        for(String q:questions){
            int idx=bestSentence(q,sentences);
            String chosen=null;
            if(idx!=-1 && sentenceAnswers.containsKey(idx)){
                for(String a:sentenceAnswers.get(idx)){
                    if(!used.contains(a)){
                        chosen=a; break;
                    }
                }
            }
            if(chosen!=null){
                used.add(chosen);
                result.put(q,chosen);
            }
        }
        return result;
    }

    private static int bestSentence(String question,List<String> sentences){
        String[] tokens=question.replace("?","").toLowerCase().split("\\W+");
        Set<String> stop=new HashSet<>(Arrays.asList(
                "what","which","where","when","who","is","are","the","of","their","to","do"));
        int best=-1,score=-1;
        for(int i=0;i<sentences.size();i++){
            int s=0;
            String line=sentences.get(i).toLowerCase();
            for(String t:tokens){
                if(stop.contains(t)||t.isEmpty()) continue;
                if(line.contains(t)) s++;
            }
            if(s>score){score=s;best=i;}
        }
        return best;
    }
}

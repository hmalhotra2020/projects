package analyzer;
/*
 * Question 1 - Modern Solution
 * Java 25
 *
 * Idea:
 * Score every (Question, Answer) pair instead of assuming one answer per sentence.
 * The answer whose containing sentence best matches the question keywords wins.
 */
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

public class ModernSolution {

    record Candidate(String answer,int sentence){}

    public static List<String> solve(String paragraph,List<String> questions,List<String> answers){
        var sentences=List.of(paragraph.split("\\.\\s*"));

        var candidates=answers.stream()
                .flatMap(a->IntStream.range(0,sentences.size())
                        .filter(i->Pattern.compile(Pattern.quote(a),Pattern.CASE_INSENSITIVE)
                                .matcher(sentences.get(i)).find())
                        .mapToObj(i->new Candidate(a,i)))
                .toList();

        var used=new HashSet<String>();
        var output=new ArrayList<String>();

        for(var q:questions){
            Candidate best=null;
            int bestScore=-1;

            for(var c:candidates){
                if(used.contains(c.answer())) continue;
                int score=score(q,sentences.get(c.sentence()),c.answer());
                if(score>bestScore){
                    bestScore=score;
                    best=c;
                }
            }

            if(best!=null){
                used.add(best.answer());
                output.add(best.answer());
            }else{
                output.add("");
            }
        }
        return output;
    }

    static int score(String question,String sentence,String answer){
        var stop=Set.of("what","which","where","when","who","is","are","the","of","their","to","do");
        int score=0;
        for(var token:question.toLowerCase().replace("?","").split("\\W+")){
            if(token.isBlank()||stop.contains(token)) continue;
            if(sentence.toLowerCase().contains(token)) score+=2;
            if(answer.toLowerCase().contains(token)) score+=3;
        }
        return score;
    }
}

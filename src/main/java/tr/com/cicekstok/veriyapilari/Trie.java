package tr.com.cicekstok.veriyapilari;

import java.util.ArrayList;
import java.util.List;

/**
 * Trie veri yapısı:
 * Ürün adlarında hızlı arama ve autocomplete işlemleri için kullanılır.
 * Özellikle öğretmenlerin soracağı "isim arama" sorularında yüksek performans sağlar.
 */

public class Trie {

    private static class Node {
        Node[] children = new Node[26];
        boolean isEnd;
        String fullWord;
    }

    private final Node root = new Node();

    private int indexOf(char c) {
        return Character.toLowerCase(c) - 'a';
    }

    public void insert(String word) {
        Node curr = root;
        for (char c : word.toLowerCase().toCharArray()) {
            if (c < 'a' || c > 'z') continue;
            int idx = indexOf(c);
            if (curr.children[idx] == null)
                curr.children[idx] = new Node();
            curr = curr.children[idx];
        }
        curr.isEnd = true;
        curr.fullWord = word;
    }

    public List<String> searchPrefix(String prefix) {
        List<String> result = new ArrayList<>();
        Node curr = root;

        for (char c : prefix.toLowerCase().toCharArray()) {
            if (c < 'a' || c > 'z') continue;
            int idx = indexOf(c);
            if (curr.children[idx] == null)
                return result;
            curr = curr.children[idx];
        }

        collect(curr, result);
        return result;
    }

    private void collect(Node node, List<String> list) {
        if (node == null) return;
        if (node.isEnd) list.add(node.fullWord);

        for (Node child : node.children) {
            collect(child, list);
        }
    }
}

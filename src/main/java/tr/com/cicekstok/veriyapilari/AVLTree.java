package tr.com.cicekstok.veriyapilari;

/**
 * AVL Tree veri yapısı:
 * Bu sınıf, ürünleri ID'ye göre hızlı arama, ekleme ve silme işlemleri için
 * dengeli bir ikili arama ağacı (AVL) yapısı uygular.
 *
 *
 * AVL Tree seçilmesinin sebebi arama, ekleme ve silme işlemlerinin
 * her zaman O(log n) zaman karmaşıklığında yapılabilmesidir.
 */

public class AVLTree<T extends Comparable<T>> {

    private Node<T> root;

    // İç düğüm sınıfı
    private static class Node<T> {
        T data;
        Node<T> left;
        Node<T> right;
        int height;

        Node(T data) {
            this.data = data;
            this.height = 1;
        }
    }

    // Yükseklik hesaplama
    private int height(Node<T> node) {
        return node == null ? 0 : node.height;
    }

    // Denge faktörü (balance factor)
    private int getBalance(Node<T> node) {
        if (node == null) return 0;
        return height(node.left) - height(node.right);
    }

    // Sağ döndürme
    private Node<T> rotateRight(Node<T> y) {
        Node<T> x = y.left;
        Node<T> T2 = x.right;

        x.right = y;
        y.left = T2;

        y.height = Math.max(height(y.left), height(y.right)) + 1;
        x.height = Math.max(height(x.left), height(x.right)) + 1;

        return x;
    }

    // Sol döndürme
    private Node<T> rotateLeft(Node<T> x) {
        Node<T> y = x.right;
        Node<T> T2 = y.left;

        y.left = x;
        x.right = T2;

        x.height = Math.max(height(x.left), height(x.right)) + 1;
        y.height = Math.max(height(y.left), height(y.right)) + 1;

        return y;
    }

    // -----------------------------
    // EKLEME
    // -----------------------------
    public void insert(T data) {
        root = insertNode(root, data);
    }

    private Node<T> insertNode(Node<T> node, T data) {
        if (node == null) return new Node<>(data);

        int cmp = data.compareTo(node.data);

        if (cmp < 0)
            node.left = insertNode(node.left, data);
        else if (cmp > 0)
            node.right = insertNode(node.right, data);
        else
            return node; // aynı değer varsa ekleme

        node.height = 1 + Math.max(height(node.left), height(node.right));
        int balance = getBalance(node);

        // Denge bozulduysa 4 durum:

        // 1) Sol-Sol
        if (balance > 1 && data.compareTo(node.left.data) < 0)
            return rotateRight(node);

        // 2) Sağ-Sağ
        if (balance < -1 && data.compareTo(node.right.data) > 0)
            return rotateLeft(node);

        // 3) Sol-Sağ
        if (balance > 1 && data.compareTo(node.left.data) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // 4) Sağ-Sol
        if (balance < -1 && data.compareTo(node.right.data) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    // -----------------------------
    // ARAMA
    // -----------------------------
    public boolean contains(T data) {
        return search(root, data) != null;
    }

    public T search(T data) {
        Node<T> n = search(root, data);
        return (n == null) ? null : n.data;
    }

    private Node<T> search(Node<T> node, T data) {
        if (node == null) return null;
        int cmp = data.compareTo(node.data);
        if (cmp == 0) return node;
        if (cmp < 0) return search(node.left, data);
        return search(node.right, data);
    }

    // -----------------------------
    // SİLME
    // -----------------------------
    public void delete(T data) {
        root = deleteNode(root, data);
    }

    private Node<T> deleteNode(Node<T> node, T data) {
        if (node == null) return null;

        int cmp = data.compareTo(node.data);
        if (cmp < 0)
            node.left = deleteNode(node.left, data);
        else if (cmp > 0)
            node.right = deleteNode(node.right, data);
        else {
            // 1) Tek çocuk veya 0 çocuk
            if (node.left == null)
                return node.right;
            else if (node.right == null)
                return node.left;

            // 2) 2 çocuk → successor bul
            Node<T> minNode = getMinValueNode(node.right);
            node.data = minNode.data;
            node.right = deleteNode(node.right, minNode.data);
        }

        node.height = Math.max(height(node.left), height(node.right)) + 1;
        int balance = getBalance(node);

        // Denge bozulan 4 durum
        if (balance > 1 && getBalance(node.left) >= 0)
            return rotateRight(node);

        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        if (balance < -1 && getBalance(node.right) <= 0)
            return rotateLeft(node);

        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    private Node<T> getMinValueNode(Node<T> node) {
        Node<T> current = node;
        while (current.left != null)
            current = current.left;
        return current;
    }

    // Ağaç boş mu?
    public boolean isEmpty() {
        return root == null;
    }
}

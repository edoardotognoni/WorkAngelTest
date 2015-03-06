package com.workangel.tech.test.hierarchy;

import android.os.Parcel;
import android.os.Parcelable;
import com.workangel.tech.test.database.bean.Employee;

import java.util.ArrayList;
import java.util.List;

/**
 * Node of a hierarchy tree. Each Node holds a reference to its parent and its children
 */
public class Node implements Parcelable {
    private static final String TAG = Node.class.getSimpleName();
    /**
     * Node data
     */
    private Employee data;
    /**
     * Parent node
     */
    private Node parent;
    /**
     * Children nodes
     */
    private List<Node> children;


    public Node() {

    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void setChildren(List<Node> children) {
        this.children = children;
    }

    public Employee getData() {
        return data;
    }

    public void setData(Employee data) {
        this.data = data;
    }


    protected Node(Parcel in) {
        data = in.readParcelable(Employee.class.getClassLoader());
        parent = in.readParcelable(Node.class.getClassLoader());
        if (in.readByte() == 0x01) {
            children = new ArrayList<Node>();
            in.readList(children, Node.class.getClassLoader());
        } else {
            children = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(data,flags);
        dest.writeParcelable(parent,flags);
        if (children == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(children);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Node> CREATOR = new Parcelable.Creator<Node>() {
        @Override
        public Node createFromParcel(Parcel in) {
            return new Node(in);
        }

        @Override
        public Node[] newArray(int size) {
            return new Node[size];
        }
    };
}
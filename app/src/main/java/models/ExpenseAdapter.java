package models;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hasnaoui.myapplication.R;

import java.util.List;

import models.Expense;

public class ExpenseAdapter extends RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder> {

    private Context context;
    private List<Expense> expenseList;
    private OnItemClickListener listener; // Interface for button clicks

    // Interface for handling button clicks
    public interface OnItemClickListener {
        void onDeleteClick(Expense expense);
    }

    public ExpenseAdapter(Context context, List<Expense> expenseList, OnItemClickListener listener) {
        this.context = context;
        this.expenseList = expenseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ExpenseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.expense_item, parent, false);
        return new ExpenseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExpenseViewHolder holder, int position) {
        Expense expense = expenseList.get(position);
        holder.textTitle.setText(expense.getTitle());
        holder.textPrice.setText(String.valueOf(expense.getAmount())+" Dh");
        holder.textDate.setText(expense.getDate().toString());

        switch (expense.getCategory().name()){
            case "LEISURE": {
                holder.categoryBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_leisure, 0);
                break;
            }case "FOOD": {
                holder.categoryBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_food, 0);
                break;
            }case "WORK": {
                holder.categoryBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_work, 0);
                break;
            }case "OTHER": {
                holder.categoryBtn.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.icon_other, 0);
                break;
            }
        }
        holder.buttonDelete.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_delete_24, 0);


        holder.buttonDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(expense);
            }
        });
    }

    @Override
    public int getItemCount() {
        return expenseList.size();
    }

    static class ExpenseViewHolder extends RecyclerView.ViewHolder {
        TextView textTitle, textPrice, textDate;
        Button buttonDelete,categoryBtn;

        public ExpenseViewHolder(@NonNull View itemView) {
            super(itemView);
            textTitle = itemView.findViewById(R.id.textTitle);
            textPrice = itemView.findViewById(R.id.textPrice);
            textDate = itemView.findViewById(R.id.textDate);
            buttonDelete = itemView.findViewById(R.id.buttonDelete);
            categoryBtn = itemView.findViewById(R.id.categoryicon);
        }
    }
}
package com.deighd.school58;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.deighd.school58.balance.Balance;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private List<Balance> balances;

    public RecyclerViewAdapter(List<Balance> balances) {
        this.balances = balances;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(
                R.layout.recyclerview_item_balance,
                viewGroup,
                false
        );
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NotNull ViewHolder viewHolder, int i) {
        Balance balance = balances.get(i);

        viewHolder.name.setText(balance.getName());
        viewHolder.residue.setText(balance.getResidueWithCurrency());

        if(balance.getPreviousResidue() != null
                && !balance.getResidue().equals(balance.getPreviousResidue())) {
            viewHolder.lastChangeTitle.setVisibility(View.VISIBLE);
            viewHolder.previousResidue.setVisibility(View.VISIBLE);
            String residueStatus;
            Float residueLastChange;
            Context context = viewHolder.itemView.getContext();
            if(balance.getResidue() > balance.getPreviousResidue()) {
                residueStatus = context.getString(R.string.residue_status_enrolled);
                residueLastChange = balance.getResidue() - balance.getPreviousResidue();
                viewHolder.previousResidue.setBackgroundResource(R.color.green);
            } else {
                residueStatus = context.getString(R.string.residue_status_spent);
                residueLastChange = balance.getPreviousResidue() - balance.getResidue();
                viewHolder.previousResidue.setBackgroundResource(R.color.red);
            }
            viewHolder.previousResidue.setText(
                    context.getString(
                            R.string.previous_residue_message,
                            residueStatus,
                            residueLastChange.toString(),
                            balance.getCurrency()
                    )
            );
        }
    }

    @Override
    public int getItemCount() {
        return balances.size();
    }

    public void addItems(List<Balance> balances) {
        this.balances = balances;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView residue;
        private TextView previousResidue;
        private TextView lastChangeTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.recycler_view_item_name);
            residue = itemView.findViewById(R.id.recycler_view_item_residue);
            previousResidue = itemView.findViewById(R.id.recycler_view_item_last_change);
            lastChangeTitle = itemView.findViewById(R.id.recycler_view_item_last_change_title);
        }
    }
}

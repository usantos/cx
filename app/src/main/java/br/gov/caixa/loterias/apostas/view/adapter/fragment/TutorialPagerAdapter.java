package br.gov.caixa.loterias.apostas.view.adapter.fragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

public class TutorialPagerAdapter extends FragmentPagerAdapter {
	private List<Fragment> list;

	public TutorialPagerAdapter(FragmentManager fragmentManager, List<Fragment> list) {
		super(fragmentManager);
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Fragment getItem(int position) {
		return list.get(position);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return "Page " + position;
	}
}
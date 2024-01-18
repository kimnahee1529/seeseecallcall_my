package com.sparta.seeseecallcall

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sparta.seeseecallcall.Constants.TAG_LIST
import com.sparta.seeseecallcall.data.Contact
import com.sparta.seeseecallcall.data.ContactManager.contactBookmarkList
import com.sparta.seeseecallcall.data.ContactManager.contactList
import com.sparta.seeseecallcall.databinding.FragmentContactListBinding

class ContactListFragment : Fragment(), OnFavoriteChangeListener, AddContactDialogFragment.OnAddContactListner {

    private var _binding: FragmentContactListBinding? = null
    private val binding get() = _binding!!

    private val adapter by lazy { MyAdapter(contactList) }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentContactListBinding.inflate(inflater, container, false)

        binding.recyclerviewList.adapter = adapter
        binding.recyclerviewList.layoutManager = LinearLayoutManager(context)
        binding.recyclerviewList.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayout.VERTICAL
            )
        )

        adapter.itemClick = object : MyAdapter.ItemClick {
            override fun onClick(view: View, position: Int) {
                Log.d(Constants.TAG_LIST, "position: $position")

                val contactData = contactList[position]
                val contactDetailFragment = ContactDetailFragment.newInstance(contactData)
                Log.d("보내는 Detail 프래그먼트", contactList[position].mbti)

                contactDetailFragment.listener = this@ContactListFragment
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, contactDetailFragment)
                    .addToBackStack(null)
                    .commit()

            }

            override fun onStarClick(view: View, position: Int) {
                Log.d(Constants.TAG, contactList[position].favorite.toString())
                contactList[position].run {
                    favorite = !favorite
                    if (favorite) {
                        contactBookmarkList.add(this)
                        contactList[position].favorite = true
                    }
                    else {
                        contactBookmarkList.remove(this)
                        contactList[position].favorite = false
                    }
                }
                contactBookmarkList.sortBy { it.name }
                adapter.notifyDataSetChanged()
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(text: Editable?) {
                Log.d(TAG_LIST, "afterTextChanged, ${text.toString()}")

               adapter.changeDataset(
                    if (text.isNullOrBlank()) contactList
                    else getFilteredList(text.toString())
                )
            }
        })

        binding.floatingBtn.setOnClickListener {
            Log.d(TAG_LIST, "floating action button clicked")

            requireActivity().supportFragmentManager
                .beginTransaction()
                .add(R.id.nav_host_fragment,AddContactDialogFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onFavoriteChanged(contact: Contact) {
        val changedPosition = contactList.indexOf(contact)
        adapter.notifyItemChanged(changedPosition)
    }

    override fun onAddContact() {
        Log.d(TAG_LIST, "ContactListFragmentList oaAddContact")
        adapter?.notifyDataSetChanged()
    }

    override fun onResume(){
        Log.d(TAG_LIST, "ContactListFragmentList onResume()")
        adapter?.notifyDataSetChanged()
        super.onResume()
    }

    private fun getFilteredList(searchText: String): MutableList<Contact> {
        val filteredList = mutableListOf<Contact>()
        return filteredList.apply {
            contactList.forEach {
                //이름으로 검색
                if (it.name.contains(searchText)) add(it)
                //mbti로 검색
                if (it.mbti.contains(searchText.toUpperCase())) add(it)
            }
        }
    }

}
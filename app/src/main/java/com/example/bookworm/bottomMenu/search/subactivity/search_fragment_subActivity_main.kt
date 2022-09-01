package com.example.bookworm.bottomMenu.search.subactivity

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.widget.ViewPager2
import com.example.bookworm.R
import com.example.bookworm.databinding.SubactivitySearchMainBinding
import com.google.android.material.tabs.TabLayoutMediator

//검색 창을 누르거나 하는 경우 나타나는 화면
//검색 결과를 보여주는 리사이클러뷰가 위치할 예정
//검색 아이템의 세부 내용은 subActivity_result 에서 담당
class search_fragment_subActivity_main : AppCompatActivity() {
    var binding: SubactivitySearchMainBinding? = null
    var context: Context? = null
    var fm: FragmentManager? = null
    var searchPageBookFragment: SearchPageBookFragment? = null
    var isFirst =true
    //    BookAdapter bookAdapter;
    private var option_idx = 0 //검색 옵션을 선택하기 위한 변수
    private val type = arrayOf("Keyword", "Title", "Author", "Publisher")
    private val items = arrayOf("제목+저자", "제목", "저자", "출판사")
    private var querys: HashMap<String, String>? = null
    private val searchList = ArrayList<String>()

    //    public ArrayList<Book> bookList;
    //    Module module;
    //    public boolean isLoading = false; //스크롤을 당겨서 추가로 로딩 중인지 여부를 확인하는 변수
    //    int count = 0, page = 0, check = 0;
    //    final int CPP = 10; //Contents Per Page : 페이지당 보이는 컨텐츠의 개수
    var classindex = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = SubactivitySearchMainBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        context = this
        fm = supportFragmentManager
        val adapter = SearchPageAdapter(fm!!, lifecycle)
        binding!!.viewpager.adapter = adapter
        TabLayoutMediator(binding!!.tabLayout, binding!!.viewpager) { tab, pos ->
            when (pos) {
                0 -> tab.text = "도서"
                1 -> tab.text = "피드"
                2 -> tab.text = "챌린지"
                else -> tab.text = "유저"
            }
        }.attach()

        classindex = intent.getIntExtra("classindex", 0)

        //spinner를 위한 adapter 생성
        val dap = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        binding!!.spinner1.adapter = dap //Adapter 적용(Spinner에 값 세팅)

        //각 이벤트별 리스너
        //옵션 선택
        binding!!.spinner1.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, idx: Int, l: Long) {
                option_idx = idx //Spinner에서 아이템 선택시, 옵션 인덱스를 변경하도록 함.
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }


        //이전으로 돌아가기
        binding!!.btnBefore.setOnClickListener {
            finish() //이전으로 돌아감.
        }

        searchList.add("")
        //검색 기능
        //검색 버튼을 누른 이후에, 결과를 표출한다.(RecyclerView)
        binding!!.edtSearch.setOnEditorActionListener { textView, id, keyEvent ->

            if (id == EditorInfo.IME_ACTION_SEARCH) {
                try {
                    setItems()
                    searchList.add(textView.text.toString())
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
            false
        }
        binding!!.edtSearch.setOnFocusChangeListener { view, hasFocus
            ->
            //포커스를 잡은 경우
            if (!hasFocus &&
                    (binding!!.edtSearch.text.isNotEmpty() || searchList.last() != "") && !isFirst){
                binding!!.llResult.visibility = View.VISIBLE
                binding!!.llAssociation.visibility = View.GONE
            }
                else {
                binding!!.llResult.visibility = View.GONE
                binding!!.llAssociation.visibility = View.VISIBLE

            }


        }

        binding!!.setting.setOnClickListener{
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding!!.edtSearch.windowToken, 0)
            binding!!.edtSearch.clearFocus()
        }
        binding!!.llAssociation.setOnClickListener {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding!!.edtSearch.windowToken, 0)
            binding!!.edtSearch.clearFocus()
        }

        binding!!.btnSearch.setOnClickListener {
            try {
                searchList.add(binding!!.edtSearch.text.toString())
                setItems()
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }
        //리사이클러뷰 관련 설정
    } //onCreate End

    //아이템 세팅
    @Throws(InterruptedException::class)
    fun setItems() {
        binding!!.llResult.visibility = View.VISIBLE
        isFirst = false
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding!!.edtSearch.windowToken, 0)
        binding!!.edtSearch.clearFocus()
        if (!(binding!!.edtSearch.text.toString().replace(" ".toRegex(), "") == "")) {
            querys = HashMap()
            querys!!["Query"] = binding!!.edtSearch.text.toString()
            querys!!["QueryType"] = type[option_idx]
            //기본값
            querys!!["ttbkey"] = getString(R.string.ttbKey)
            querys!!["MaxResults"] = "10" //최대 길이
            querys!!["output"] = "js"
            querys!!["SearchTarget"] = "Book"
            querys!!["Version"] = "20131101"
            val url = "http://www.aladin.co.kr/ttb/api/" //API 이용

//            module = new Module(this, url, querys);
//            module.connect(0);
            binding!!.tabLayout.getTabAt(0)!!.select()
            searchPageBookFragment = (((context as search_fragment_subActivity_main).findViewById<View>(R.id.viewpager) as ViewPager2).adapter as SearchPageAdapter?)!!.getItem(0) as SearchPageBookFragment
            searchPageBookFragment!!.searchBook(context, url, querys)
        } else {
            Toast.makeText(applicationContext, "검색어를 입력해 주세요", Toast.LENGTH_SHORT).show()
        }
    } //
    //    //리사이클러뷰를 초기화
    //    private void initRecyclerView() {
    //        binding.recyclerView.setAdapter(bookAdapter);
    //        initScrollListener(); //무한스크롤
    //    }
    //
    //    //리사이클러뷰 스크롤 초기화
    //    private void initScrollListener() {
    //        binding.recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
    //            @Override
    //            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
    //                super.onScrollStateChanged(recyclerView, newState);
    //            }
    //
    //            @Override
    //            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
    //                super.onScrolled(recyclerView, dx, dy);
    //                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
    //                int lastVisibleItemPosition = layoutManager.findLastCompletelyVisibleItemPosition();
    //                if (!isLoading) {
    //                    try {
    //                        if (layoutManager != null && lastVisibleItemPosition == bookAdapter.getItemCount() - 1) {
    //                            page = module.getPage();
    //                            count = module.getCount();
    //                            bookAdapter.deleteLoading();
    //                            if ((page * CPP) < count) {
    //                                module.connect(0);
    //                            }
    //                            isLoading = true;
    //                        }
    //                    } catch (NullPointerException e) {
    //
    //                    }
    //                }
    //
    //            }
    //        });
    //
    //    }
    //
    //
    //    //module에서 사용하는 함수
    //    public void moduleUpdated(JSONArray jsonArray) throws JSONException {
    //        page = module.getPage();
    //        count = module.getCount();
    //        int beforeSize = bookList.size();
    //        if (page == 1) {
    //            check = count;
    //            Log.d("cje", String.valueOf(check));
    //            bookList = new ArrayList<>(); //book을 담는 리스트 생성
    //        }
    //        if (count == 0) {
    //            //검색결과가 없을 경우엔 리사이클러 뷰를 비움.
    //            bookList = new ArrayList<>();
    //            bookAdapter = new BookAdapter(bookList, this);
    //            initRecyclerView();
    //            Toast.makeText(this, "검색결과가 없습니다", Toast.LENGTH_SHORT).show();
    //        } else {
    //            //booklist에 책을 담음
    //            //이미 한번 검색한 경우 추가 페이지 로딩하도록 설계해야 함.
    //            //아마 북리스트에 아이템을 계속 추가하면 되지 않을까,,
    //            for (int i = 0; i < jsonArray.length(); i++) {
    //                JSONObject obj = jsonArray.getJSONObject(i);
    //                Book book = new Book(obj.getString("title"), obj.getString("categoryName"), obj.getString("description"), obj.getString("publisher"), obj.getString("author"), obj.getString("cover"), obj.getString("itemId"));
    //                bookList.add(book);
    //            }
    //            if (check > 20 && page < 20) {
    //                bookList.add(new Book("", "", "", "", "", ""));
    //                this.check = count - bookList.size();
    //            } else isLoading = true;
    //
    //            if (page != 1 && page < 20) {
    //                isLoading = false;
    //                bookAdapter.notifyItemRangeChanged(beforeSize, bookList.size() - beforeSize);
    //            } else {
    //                bookAdapter = new BookAdapter(bookList, this);
    //                bookAdapter.setListener(new OnBookItemClickListener() {
    //                    @Override
    //                    public void onItemClick(BookAdapter.ItemViewHolder holder, View view, int position) {
    //
    //                        if (classindex == 0) {
    //                            Intent intent = new Intent(getApplicationContext(), search_fragment_subActivity_result.class);
    //
    //                            intent.putExtra("itemid", bookList.get(position).getItemId());
    //                            intent.putExtra("data", bookList.get(position));
    //                            setResult(Activity.RESULT_OK, intent);
    //
    //                            startActivity(intent);
    //                        } else if (classindex == 1) {
    //                            finish();
    //                        } else if (classindex == 2) {
    //                            Intent intent = new Intent();
    //                            intent.putExtra("data", bookList.get(position));
    //                            setResult(Activity.RESULT_OK, intent);
    //                            finish();
    //                        }
    //                    }
    //
    //                    @Override
    //                    public void onItemClick(RecomBookAdapter.ItemViewHolder holder, View view, int position) {
    //
    //                    }
    //                });
    //                initRecyclerView(); //initialize RecyclerView
    //            }
    //            this.page++;
    //            module.setPage(page);
    //        }
    //
    //    }
} //subActivity End
